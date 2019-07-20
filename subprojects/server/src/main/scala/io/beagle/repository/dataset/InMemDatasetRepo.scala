package io.beagle.repository.dataset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}

case class InMemDatasetRepo(db: Ref[IO, Map[DatasetId, DatasetItem]], counter: Ref[IO, Long]) extends DatasetRepo {
  def create(seqSet: Dataset): IO[DatasetItem] = {
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = DatasetId(c)
      view = DatasetItem(id, seqSet)
      _ <- db.update(store => store + ( id -> view ))
    } yield view
  }

  def update(id: DatasetId, seqSet: Dataset): IO[DatasetItem] = {
    db.modify(map => {
      val view = DatasetItem(id, seqSet)
      (map.updated(id, view), view)
    })
  }

  def find(id: DatasetId): IO[Option[DatasetItem]] = db.get.map( _.get(id))

  def delete(id: DatasetId): IO[Unit] = db.update(map => map - id)

}
