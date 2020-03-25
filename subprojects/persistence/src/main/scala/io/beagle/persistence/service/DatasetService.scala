package io.beagle.persistence.service

import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetItem}
import io.beagle.persistence.repository.dataset.DatasetRepo

object DatasetService {

  case class DatasetAlreadyExists(dataset: Dataset) extends Exception(dataset.name)

  case class DatasetDoesNotExist(dataset: Dataset) extends Exception(dataset.name)

  def createTable(): ConnectionIO[Int] = DatasetRepo.createTable()

  def create(dataset: Dataset): ConnectionIO[DatasetItem] = DatasetRepo.create(dataset)

  def deleteAll(): ConnectionIO[Unit] = DatasetRepo.deleteAll()

}
