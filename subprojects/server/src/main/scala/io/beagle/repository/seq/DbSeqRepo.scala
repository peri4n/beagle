package io.beagle.repository.seq

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Seq, SeqId, SeqItem}

case object DbSeqRepo extends SeqRepo {

  val TableName = "sequences"

  def create(seq: Seq): ConnectionIO[SeqItem] = {
    sql"INSERT INTO $TableName (identifier, sequence) VALUES (${ seq.identifier }, ${ seq.sequence })".update
      .withUniqueGeneratedKeys[SeqItem]("id", "identifier", "sequence")
  }

  def update(id: SeqId, seq: Seq): ConnectionIO[SeqItem] = {
    sql"UPDATE $TableName SET (identifier, sequence) WHERE id = $id".update
      .withUniqueGeneratedKeys[SeqItem]("id", "identifier", "sequence")
  }

  def findById(id: SeqId): ConnectionIO[Option[SeqItem]] = {
    sql"SELECT * FROM $TableName WHERE id = $id".query[SeqItem]
      .option
  }

  def delete(id: SeqId): ConnectionIO[Unit] = {
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .map(_ => ())
  }
}
