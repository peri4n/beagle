package io.beagle.repository.seqset

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.DatabaseSettings
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetView}

class DbSeqSetRepo(settings: DatabaseSettings) extends SeqSetRepo {

  import DbSeqSetRepo._

  def create(seqSet: SeqSet): IO[SeqSetView] =
    sql"INSERT INTO $TableName (name) VALUES (${seqSet.name})".update
      .withUniqueGeneratedKeys[SeqSetView]("id", "name")
      .transact(settings.transactor)

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetView] =
    sql"UPDATE $TableName SET name = ${seqSet.name} WHERE id = $id".update
      .withUniqueGeneratedKeys[SeqSetView]("id", "name")
      .transact(settings.transactor)

  def find(id: SeqSetId): IO[Option[SeqSetView]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[SeqSetView]
      .option
      .transact(settings.transactor)

  def delete(id: SeqSetId): IO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(settings.transactor)
      .map(_ => Unit)
}

object DbSeqSetRepo {

  val TableName = "sequence_set"

}
