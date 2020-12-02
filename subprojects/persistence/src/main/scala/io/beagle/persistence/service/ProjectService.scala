package io.beagle.persistence.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Project, ProjectId, ProjectItem, User}
import io.beagle.persistence.repository.project.ProjectRepository

case class ProjectService(userService: UserService, projectRepo: ProjectRepository) {

  case class ProjectAlreadyExists(project: Project) extends Exception(project.name)

  case class ProjectDoesNotExist(msg: String) extends Exception(msg)

  def createTable(): ConnectionIO[Int] = projectRepo.createTable()

  def create(project: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- userService.findByIdStrict(project.ownerId)
      maybeProject <- projectRepo.findByName(project.name, owner.id)
      create <- maybeProject.fold(projectRepo.create(project)) { _ =>
        Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(project))
      }
    } yield create
  }

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] = projectRepo.findById(id)

  def findByIdStrict(id: ProjectId): ConnectionIO[ProjectItem] = projectRepo.findById(id).flatMap { maybeProject =>
    maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectDoesNotExist("foo"))) { project =>
      Sync[ConnectionIO].pure(project)
    }
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
      _ <- maybeProject.fold(Sync[ConnectionIO].raiseError[Unit](ProjectDoesNotExist("foo"))) { projectItem =>
        projectRepo.delete(projectItem.id)
      }
    } yield ()
  }

  def deleteAll(): ConnectionIO[Unit] = projectRepo.deleteAll()
}
