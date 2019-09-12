package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.{Repositories, Services}
import io.beagle.domain.{Project, ProjectId, ProjectItem, User, UserId, UserItem}
import io.beagle.repository.project.ProjectRepo
import io.beagle.service.ProjectService.{ProjectAlreadyExists, ProjectDoesNotExist}
import io.beagle.service.UserService.UserDoesNotExist

object ProjectService {

  def instance =
    for {
      projectRepo <- Repositories.project
      userService <- Services.user
    } yield ProjectService(projectRepo, userService)

  case class ProjectAlreadyExists(project: Project) extends Exception(project.name)

  case class ProjectDoesNotExist(msg: String) extends Exception(msg)

}

case class ProjectService(repo: ProjectRepo, userService: UserService) {

  def create(project: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- userService.findByIdStrict(project.ownerId)
      maybeProject <- repo.findByName(project.name, owner.id)
      create <- maybeProject.fold(repo.create(project)) { _ =>
        Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(project))
      }
    } yield create
  }

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] = repo.findById(id)

  def findByIdStrict(id: ProjectId): ConnectionIO[ProjectItem] = repo.findById(id).flatMap { maybeProject =>
    maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectDoesNotExist("foo"))) { project =>
      Sync[ConnectionIO].pure(project)
    }
  }

  def update(oldProject: Project, newProject: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- userService.findByIdStrict(newProject.ownerId)
      maybeProject <- repo.findByName(oldProject.name, owner.id)
      update <- maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(newProject))) { projectItem =>
        repo.update(projectItem.id, newProject)
      }
    } yield update
  }

  def delete(project: Project, owner: User): ConnectionIO[Unit] = {
    for {
      ownerItem <- userService.findByNameStrict(owner.name)
      maybeProject <- repo.findByName(project.name, ownerItem.id)
      _ <- maybeProject.fold(Sync[ConnectionIO].raiseError[Unit](ProjectDoesNotExist("foo"))) { projectItem =>
        repo.delete(projectItem.id)
      }
    } yield ()
  }

  def deleteAll(): ConnectionIO[Unit] = repo.deleteAll()
}
