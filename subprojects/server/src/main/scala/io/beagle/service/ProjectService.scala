package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.{Repositories, Services}
import io.beagle.domain.{Project, ProjectItem, User}
import io.beagle.repository.project.ProjectRepo
import io.beagle.service.ProjectService.ProjectAlreadyExists

object ProjectService {

  def instance =
    for {
      projectRepo <- Repositories.project
      userService <- Services.user
    } yield ProjectService(projectRepo, userService)

  case class ProjectAlreadyExists(project: Project) extends Exception(project.name)

  case class ProjectDoesNotExist(project: Project) extends Exception(project.name)

}

case class ProjectService(projectRepo: ProjectRepo, userService: UserService) {

  def create(project: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- userService.findByIdStrict(project.ownerId)
      maybeProject <- projectRepo.findByName(project.name, owner.id)
      create <- maybeProject.fold(projectRepo.create(project)) { _ =>
        Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(project))
      }
    } yield create
  }

  def update(oldProject: Project, newProject: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- userService.findByIdStrict(newProject.ownerId)
      maybeProject <- projectRepo.findByName(oldProject.name, owner.id)
      update <- maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(newProject))) { projectItem =>
        projectRepo.update(projectItem.id, newProject)
      }
    } yield update
  }

  def delete(project: Project, owner: User): ConnectionIO[Unit] = {
    for {
      ownerItem <- userService.findByNameStrict(owner.name)
      maybeProject <- projectRepo.findByName(project.name, ownerItem.id)
      _ <- maybeProject.fold(Sync[ConnectionIO].raiseError[Unit](ProjectService.ProjectDoesNotExist(project))) { projectItem =>
        projectRepo.delete(projectItem.id)
      }
    } yield ()
  }

  def deleteAll(): ConnectionIO[Unit] = projectRepo.deleteAll()
}
