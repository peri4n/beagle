package io.beagle.repository.seq
import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain
import io.beagle.domain.{SeqId, Seq, SeqView}

class InMemSeqRepo(db: Ref[IO, Map[SeqId, SeqView]], counter: Ref[IO, Long]) extends SeqRepo {

  def create(seq: domain.Seq): IO[SeqView] = {
    for {
      c <- counter.get
      _ <- counter.update( _ + 1)
      id = SeqId(c)
      view = SeqView(id, seq)
      _ <- db.update(store => store + (id -> view))
    } yield view
  }

  def update(id: SeqId, seq: Seq): IO[SeqView] = {
    db.modify(map => {
      val view = SeqView(id, seq)
      (map.updated(id, view), view)
    })
  }

  def find(id: SeqId): IO[Option[SeqView]] = {
    for {
      map <- db.get
    } yield map.get(id)
  }

  def delete(id: SeqId): IO[Unit] = db.update(map => map - id)
}
