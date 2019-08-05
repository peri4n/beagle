package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.Repositories
import io.beagle.domain.{Project, ProjectItem}
import io.beagle.repository.project.ProjectRepo
import io.beagle.service.ProjectService.{ProjectAlreadyExists, ProjectDoesNotExist}

case class ProjectService(repo: ProjectRepo) {

  def create(project: Project): ConnectionIO[ProjectItem] = {
    repo.findByName(project.name).flatMap { maybeProject =>
      maybeProject.fold(repo.create(project)) { _ =>
        Sync[ConnectionIO].raiseError(ProjectAlreadyExists(project))
      }
    }
  }

  def update(oldProject: Project, newProject: Project): ConnectionIO[ProjectItem] = {
    repo.findByName(oldProject.name).flatMap {
      case Some(projectItem) => repo.update(projectItem.id, newProject)
      case None              => Sync[ConnectionIO].raiseError[ProjectItem](ProjectAlreadyExists(newProject))
    }
  }

  def delete(project: Project): ConnectionIO[Unit] = {
    repo.findByName(project.name).flatMap {
      case Some(projectItem) => repo.delete(projectItem.id)
      case None              => Sync[ConnectionIO].raiseError[Unit](ProjectDoesNotExist(project))
    }
  }
}

object ProjectService {

  def instance = Repositories.project map { ProjectService(_) }

  case class ProjectAlreadyExists(project: Project) extends Exception(project.name)

  case class ProjectDoesNotExist(project: Project) extends Exception(project.name)

}
