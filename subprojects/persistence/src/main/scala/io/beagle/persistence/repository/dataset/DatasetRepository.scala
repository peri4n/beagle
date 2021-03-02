package io.beagle.persistence.repository.dataset

import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}

trait DatasetRepository {
  def createTable(): ConnectionIO[Int]

  def create(dataset: Dataset): ConnectionIO[DatasetItem]

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem]

  def findById(id: DatasetId): ConnectionIO[Option[DatasetItem]]

  def findByName(name: String, project: ProjectId): ConnectionIO[Option[DatasetItem]]

  def delete(id: DatasetId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]

}
