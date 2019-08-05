package io.beagle.repository.dataset

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}

trait DatasetRepo {

  def create(seqSet: Dataset): ConnectionIO[DatasetItem]

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem]

  def find(id: DatasetId): ConnectionIO[Option[DatasetItem]]

  def delete(id: DatasetId): ConnectionIO[Unit]
}

object DatasetRepo {

  def inMemory =
    InMemDatasetRepo(
      Ref.unsafe[IO, Map[DatasetId, DatasetItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )
}
