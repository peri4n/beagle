package io.beagle.repository.seq

import cats.effect.IO
import io.beagle.domain._

trait SeqRepo {

  def create(seq: Seq): IO[SeqView]

  def update(id: SeqId, seq: Seq): IO[SeqView]

  def find(id: SeqId): IO[Option[SeqView]]

  def delete(id: SeqId): IO[Unit]
}
