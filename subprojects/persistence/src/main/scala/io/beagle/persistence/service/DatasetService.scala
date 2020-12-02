package io.beagle.persistence.service

import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}
import io.beagle.persistence.repository.dataset.DatasetRepository

case class DatasetService(datasetRepo: DatasetRepository) {

  case class DatasetAlreadyExists(dataset: Dataset) extends Exception(dataset.name)

  case class DatasetDoesNotExist(dataset: Dataset) extends Exception(dataset.name)

  def delete(id: DatasetId) = datasetRepo.delete(id)

  def findById(id: DatasetId) = datasetRepo.findById(id)

  def createTable(): ConnectionIO[Int] = datasetRepo.createTable()

  def create(dataset: Dataset): ConnectionIO[DatasetItem] = datasetRepo.create(dataset)

  def deleteAll(): ConnectionIO[Unit] = datasetRepo.deleteAll()

}
