package io.beagle.repository.seqset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetView}

object SeqSetRepo {

  def inMemory =
  InMemSeqSetRepo(
    Ref.unsafe[IO, Map[SeqSetId, SeqSetView]](Map.empty),
    Ref.unsafe[IO, Long](0L)
  )

}

trait SeqSetRepo {

  def create(seqSet: SeqSet): IO[SeqSetView]

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetView]

  def find(id: SeqSetId): IO[Option[SeqSetView]]

  def delete(id: SeqSetId): IO[Unit]
}
