package io.beagle.repository.seq

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Seq, SeqId, SeqItem}

class InMemSeqRepo(db: Ref[IO, Map[SeqId, SeqItem]], counter: Ref[IO, Long]) extends SeqRepo {

  def create(seq: Seq): IO[SeqItem] = {
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = SeqId(c)
      view = SeqItem(id, seq)
      _ <- db.update(store => store + ( id -> view ))
    } yield view
  }

  def update(id: SeqId, seq: Seq): IO[SeqItem] = {
    db.modify(map => {
      val view = SeqItem(id, seq)
      (map.updated(id, view), view)
    })
  }

  def find(id: SeqId): IO[Option[SeqItem]] = db.get.map(_.get(id))

  def delete(id: SeqId): IO[Unit] = db.update(map => map - id)
}
