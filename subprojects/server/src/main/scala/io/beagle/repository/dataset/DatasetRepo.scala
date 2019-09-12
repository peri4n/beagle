package io.beagle.repository.dataset

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}

trait DatasetRepo {

  def create(seqSet: Dataset): ConnectionIO[DatasetItem]

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem]

  def findById(id: DatasetId): ConnectionIO[Option[DatasetItem]]

  def findByName(name: String, project: ProjectId): ConnectionIO[Option[DatasetItem]]

  def delete(id: DatasetId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}

object DatasetRepo {
  def inDB: DatasetRepo = DbDatasetRepo

  def inMemory =
    InMemDatasetRepo(
      Ref.unsafe[IO, Map[DatasetId, DatasetItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )
}
