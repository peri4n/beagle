package io.beagle.repository.seq

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.{DatabaseSettings, Settings}
import io.beagle.domain.{Seq, SeqId, SeqView}

object DbSeqRepo {

  val TableName = "sequence"

  val instance = Settings.database map { DbSeqRepo(_) }
}

case class DbSeqRepo(settings: DatabaseSettings) extends SeqRepo {

  import DbSeqRepo._

  def create(seq: Seq): IO[SeqView] = {
    sql"insert into $TableName (identifier, sequence) values (${seq.identifier}, ${seq.sequence})".update
      .withUniqueGeneratedKeys[SeqView]("id", "identifier", "sequence")
      .transact(settings.transactor)
  }

  def update(id: SeqId, seq: Seq): IO[SeqView] = {
    sql"UPDATE $TableName SET (identifier, sequence) WHERE id = $id".update
      .withUniqueGeneratedKeys[SeqView]("id", "identifier", "sequence")
      .transact(settings.transactor)
  }

  def find(id: SeqId): IO[Option[SeqView]] = {
    sql"SELECT * FROM $TableName WHERE id = $id".query[SeqView]
        .option
        .transact(settings.transactor)
  }

  def delete(id: SeqId): IO[Unit] = {
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(settings.transactor)
      .map(_ => Unit)
  }

}
