package io.beagle.repository.dataset

import cats.effect.concurrent.Ref
import cats.effect.{Async, IO}
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}

case class InMemDatasetRepo(db: Ref[IO, Map[DatasetId, DatasetItem]], counter: Ref[IO, Long]) extends DatasetRepo {

  def create(seqSet: Dataset): ConnectionIO[DatasetItem] = {
    Async[ConnectionIO].liftIO(
      for {
        c <- counter.get
        _ <- counter.update(_ + 1)
        id = DatasetId(c)
        view = DatasetItem(id, seqSet)
        _ <- db.update(store => store + ( id -> view ))
      } yield view
    )
  }

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem] = {
    Async[ConnectionIO].liftIO(
      db.modify(map => {
        val view = DatasetItem(id, seqSet)
        (map.updated(id, view), view)
      }))
  }

  def findById(id: DatasetId): ConnectionIO[Option[DatasetItem]] = Async[ConnectionIO].liftIO(db.get.map(_.get(id)))

  def findByName(name: String, projectId: ProjectId): ConnectionIO[Option[DatasetItem]] =
    Async[ConnectionIO].liftIO(db.get.map(_.values.find(item =>
      item.dataset.name == name && item.dataset.projectId == projectId))
    )

  def delete(id: DatasetId): ConnectionIO[Unit] = Async[ConnectionIO].liftIO(db.update(map => map - id))

  def deleteAll(): ConnectionIO[Unit] =
    Async[ConnectionIO].liftIO(db.update(_ => Map.empty))
}
