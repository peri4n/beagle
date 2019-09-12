package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.{Repositories, Services}
import io.beagle.domain.{Dataset, DatasetItem}
import io.beagle.repository.dataset.DatasetRepo
import io.beagle.service.DatasetService.DatasetAlreadyExists

object DatasetService {

  def instance = for {
    datasetRepo <- Repositories.dataset
    userService <- Services.project
  } yield DatasetService(datasetRepo, userService)

  case class DatasetAlreadyExists(dataset: Dataset) extends Exception(dataset.name)

  case class DatasetDoesNotExist(dataset: Dataset) extends Exception(dataset.name)

}

case class DatasetService(repo: DatasetRepo, projectService: ProjectService) {

  def create(dataset: Dataset): ConnectionIO[DatasetItem] = {
    for {
      project <- projectService.findByIdStrict(dataset.projectId)
      maybeProject <- repo.findByName(dataset.name, project.id)
      create <- maybeProject.fold(repo.create(dataset)) { _ =>
        Sync[ConnectionIO].raiseError[DatasetItem](DatasetAlreadyExists(dataset))
      }
    } yield create
  }

  def deleteAll(): ConnectionIO[Unit] = repo.deleteAll()
}
