package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.Repositories
import io.beagle.domain.{Seq, SeqItem}
import io.beagle.repository.seq.SeqRepo

case class SeqService(repo: SeqRepo) {

  import SeqService._

  def create(seq: Seq): ConnectionIO[SeqItem] = {
    repo.findByIdentifier(seq.identifier).flatMap { maybeProject =>
      maybeProject.fold(repo.create(seq)) { _ =>
        Sync[ConnectionIO].raiseError(SequenceAlreadyExists(seq))
      }
    }
  }

  def update(oldSeq: Seq, newSeq: Seq): ConnectionIO[SeqItem] = {
    repo.findByIdentifier(oldSeq.identifier).flatMap {
      case Some(seqItem) => repo.update(seqItem.id, newSeq)
      case None          => Sync[ConnectionIO].raiseError[SeqItem](SequenceAlreadyExists(newSeq))
    }
  }

  def delete(seq: Seq): ConnectionIO[Unit] = {
    repo.findByIdentifier(seq.identifier).flatMap {
      case Some(seqItem) => repo.delete(seqItem.id)
      case None          => Sync[ConnectionIO].raiseError[Unit](SequenceDoesNotExist(seq))
    }
  }
}

object SeqService {
  def instance = Repositories.sequence map { SeqService(_) }

  case class SequenceAlreadyExists(seq: Seq) extends Exception(seq.identifier)

  case class SequenceDoesNotExist(seq: Seq) extends Exception(seq.identifier)

}
