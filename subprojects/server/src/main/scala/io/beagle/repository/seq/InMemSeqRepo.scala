package io.beagle.repository.seq

import cats.effect.concurrent.Ref
import cats.effect.{Async, IO}
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Seq, SeqId, SeqItem}

case class InMemSeqRepo(db: Ref[IO, Map[SeqId, SeqItem]], counter: Ref[IO, Long]) extends SeqRepo {

  def create(seq: Seq): ConnectionIO[SeqItem] = {
    Async[ConnectionIO].liftIO(
      for {
        c <- counter.get
        _ <- counter.update(_ + 1)
        id = SeqId(c)
        view = SeqItem(id, seq)
        _ <- db.update(store => store + ( id -> view ))
      } yield view)
  }

  def update(id: SeqId, seq: Seq): ConnectionIO[SeqItem] = {
    Async[ConnectionIO].liftIO(
      db.modify(map => {
        val view = SeqItem(id, seq)
        (map.updated(id, view), view)
      }))
  }

  def findById(id: SeqId): ConnectionIO[Option[SeqItem]] = Async[ConnectionIO].liftIO(db.get.map(_.get(id)))

  def delete(id: SeqId): ConnectionIO[Unit] = Async[ConnectionIO].liftIO(db.update(map => map - id))
}
