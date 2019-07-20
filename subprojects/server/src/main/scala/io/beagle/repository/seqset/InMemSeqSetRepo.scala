package io.beagle.repository.seqset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetItem}

case class InMemSeqSetRepo(db: Ref[IO, Map[SeqSetId, SeqSetItem]], counter: Ref[IO, Long]) extends SeqSetRepo {
  def create(seqSet: SeqSet): IO[SeqSetItem] = {
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = SeqSetId(c)
      view = SeqSetItem(id, seqSet)
      _ <- db.update(store => store + ( id -> view ))
    } yield view
  }

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetItem] = {
    db.modify(map => {
      val view = SeqSetItem(id, seqSet)
      (map.updated(id, view), view)
    })
  }

  def find(id: SeqSetId): IO[Option[SeqSetItem]] = db.get.map( _.get(id))

  def delete(id: SeqSetId): IO[Unit] = db.update(map => map - id)

}
