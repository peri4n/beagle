package io.beagle.repository.seq

import cats.effect.IO
import io.beagle.domain._

trait SeqRepo {

  def create(seq: Seq): IO[SeqItem]

  def update(id: SeqId, seq: Seq): IO[SeqItem]

  def find(id: SeqId): IO[Option[SeqItem]]

  def delete(id: SeqId): IO[Unit]
}
