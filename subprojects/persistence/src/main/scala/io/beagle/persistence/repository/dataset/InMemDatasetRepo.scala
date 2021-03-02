package io.beagle.persistence.repository.dataset

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}

case class InMemDatasetRepo(db: Ref[IO, Map[DatasetId, DatasetItem]], counter: Ref[IO, Long]) extends DatasetRepository {
  override def createTable(): ConnectionIO[Int] = ???

  override def create(dataset: Dataset): ConnectionIO[DatasetItem] = ???

  override def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem] = ???

  override def findById(id: DatasetId): ConnectionIO[Option[DatasetItem]] = ???

  override def findByName(name: String, project: ProjectId): ConnectionIO[Option[DatasetItem]] = ???

  override def delete(id: DatasetId): ConnectionIO[Unit] = ???

  override def deleteAll(): ConnectionIO[Unit] = ???
}

object InMemDatasetRepo {
  def create(): IO[InMemDatasetRepo] =
    for {
      db <- Ref.of[IO, Map[DatasetId, DatasetItem]](Map.empty)
      counter <- Ref.of[IO, Long](1L)
    } yield new InMemDatasetRepo(db, counter)
}
