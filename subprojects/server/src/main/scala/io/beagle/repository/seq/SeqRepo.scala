package io.beagle.repository.seq

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain._

trait SeqRepo {

  def create(seq: Seq): ConnectionIO[SeqItem]

  def update(id: SeqId, seq: Seq): ConnectionIO[SeqItem]

  def find(id: SeqId): ConnectionIO[Option[SeqItem]]

  def findByIdentifier(name: String): ConnectionIO[Option[SeqItem]]

  def delete(id: SeqId): ConnectionIO[Unit]
}

object SeqRepo {

  def inMemory =
    InMemSeqRepo(
      Ref.unsafe[IO, Map[SeqId, SeqItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )

}

