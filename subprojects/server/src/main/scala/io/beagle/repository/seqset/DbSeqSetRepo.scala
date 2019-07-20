package io.beagle.repository.seqset

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.domain.{SeqSet, SeqSetId, SeqSetItem}

class DbSeqSetRepo(xa: Transactor[IO]) extends SeqSetRepo {

  import DbSeqSetRepo._

  def create(seqSet: SeqSet): IO[SeqSetItem] =
    sql"INSERT INTO $TableName (name) VALUES (${ seqSet.name })".update
      .withUniqueGeneratedKeys[SeqSetItem]("id", "name")
      .transact(xa)

  def update(id: SeqSetId, seqSet: SeqSet): IO[SeqSetItem] =
    sql"UPDATE $TableName SET name = ${ seqSet.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[SeqSetItem]("id", "name")
      .transact(xa)

  def find(id: SeqSetId): IO[Option[SeqSetItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[SeqSetItem]
      .option
      .transact(xa)

  def delete(id: SeqSetId): IO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(xa)
      .map(_ => Unit)
}

object DbSeqSetRepo {

  val TableName = "sequence_sets"

}
