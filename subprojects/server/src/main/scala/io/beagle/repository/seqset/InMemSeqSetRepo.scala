package io.beagle.repository.seqset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetView}

case class InMemSeqSetRepo(db: Ref[IO, Map[SeqSetId, SeqSetView]], counter: Ref[IO, Long]) extends SeqSetRepo {
  def create(seqSet: SeqSet): IO[SeqSetView] = {
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = SeqSetId(c)
      view = SeqSetView(id, seqSet)
      _ <- db.update(store => store + ( id -> view ))
    } yield view
  }

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetView] = {
    db.modify(map => {
      val view = SeqSetView(id, seqSet)
      (map.updated(id, view), view)
    })
  }

  def find(id: SeqSetId): IO[Option[SeqSetView]] = {
    for {
      map <- db.get
    } yield map.get(id)
  }

  def delete(id: SeqSetId): IO[Unit] = db.update(map => map - id)

}
