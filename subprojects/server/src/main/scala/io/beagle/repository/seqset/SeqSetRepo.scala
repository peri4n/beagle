package io.beagle.repository.seqset

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetItem}

object SeqSetRepo {

  def inMemory =
    InMemSeqSetRepo(
      Ref.unsafe[IO, Map[SeqSetId, SeqSetItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )

}

trait SeqSetRepo {

  def create(seqSet: SeqSet): IO[SeqSetItem]

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetItem]

  def find(id: SeqSetId): IO[Option[SeqSetItem]]

  def delete(id: SeqSetId): IO[Unit]
}
