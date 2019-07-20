package io.beagle.repository.seq

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.components.DatabaseSettings
import io.beagle.domain.{Seq, SeqId, SeqItem}

case class DbSeqRepo(xa: Transactor[IO]) extends SeqRepo {

  import DbSeqRepo._

  def create(seq: Seq): IO[SeqItem] = {
    sql"INSERT INTO $TableName (identifier, sequence) VALUES (${ seq.identifier }, ${ seq.sequence })".update
      .withUniqueGeneratedKeys[SeqItem]("id", "identifier", "sequence")
      .transact(xa)
  }

  def update(id: SeqId, seq: Seq): IO[SeqItem] = {
    sql"UPDATE $TableName SET (identifier, sequence) WHERE id = $id".update
      .withUniqueGeneratedKeys[SeqItem]("id", "identifier", "sequence")
      .transact(xa)
  }

  def find(id: SeqId): IO[Option[SeqItem]] = {
    sql"SELECT * FROM $TableName WHERE id = $id".query[SeqItem]
      .option
      .transact(xa)
  }

  def delete(id: SeqId): IO[Unit] = {
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(xa)
      .map(_ => Unit)
  }
}

object DbSeqRepo {

  val TableName = "sequences"

  val instance = DatabaseSettings.transactor map { DbSeqRepo(_) }
}
