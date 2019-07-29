package io.beagle.repository.dataset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}

trait DatasetRepo {

  def create(seqSet: Dataset): IO[DatasetItem]

  def update(id: DatasetId, seqSet: Dataset): IO[DatasetItem]

  def find(id: DatasetId): IO[Option[DatasetItem]]

  def delete(id: DatasetId): IO[Unit]
}

object DatasetRepo {

  def inMemory =
    InMemDatasetRepo(
      Ref.unsafe[IO, Map[DatasetId, DatasetItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )
}
