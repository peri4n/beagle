package io.beagle.persistence.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Project, ProjectId, ProjectItem, User}
import io.beagle.persistence.repository.project.ProjectRepo

object ProjectService {

  case class ProjectAlreadyExists(project: Project) extends Exception(project.name)

  case class ProjectDoesNotExist(msg: String) extends Exception(msg)

  def createTable(): ConnectionIO[Int] = ProjectRepo.createTable()

  def create(project: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- UserService.findByIdStrict(project.ownerId)
      maybeProject <- ProjectRepo.findByName(project.name, owner.id)
      create <- maybeProject.fold(ProjectRepo.create(project)) { _ =>
        Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(project))
      }
    } yield create
  }

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] = ProjectRepo.findById(id)

  def findByIdStrict(id: ProjectId): ConnectionIO[ProjectItem] = ProjectRepo.findById(id).flatMap { maybeProject =>
    maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectDoesNotExist("foo"))) { project =>
      Sync[ConnectionIO].pure(project)
    }
  }

  def update(oldProject: Project, newProject: Project): ConnectionIO[ProjectItem] = {
    for {
      owner <- UserService.findByIdStrict(newProject.ownerId)
      maybeProject <- ProjectRepo.findByName(oldProject.name, owner.id)
      update <- maybeProject.fold(Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(newProject))) { projectItem =>
        ProjectRepo.update(projectItem.id, newProject)
      }
    } yield update
  }

  def delete(project: Project, owner: User): ConnectionIO[Unit] = {
    for {
      ownerItem <- UserService.findByNameStrict(owner.name)
      maybeProject <- ProjectRepo.findByName(project.name, ownerItem.id)
      _ <- maybeProject.fold(Sync[ConnectionIO].raiseError[Unit](ProjectDoesNotExist("foo"))) { projectItem =>
        ProjectRepo.delete(projectItem.id)
      }
    } yield ()
  }

  def deleteAll(): ConnectionIO[Unit] = ProjectRepo.deleteAll()
}
