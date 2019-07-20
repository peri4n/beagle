package io.beagle.repository.dataset

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}

class DbDatasetRepo(xa: Transactor[IO]) extends DatasetRepo {

  import DbDatasetRepo._

  def create(seqSet: Dataset): IO[DatasetItem] =
    sql"INSERT INTO $TableName (name) VALUES (${ seqSet.name })".update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name")
      .transact(xa)

  def update(id: DatasetId, seqSet: Dataset): IO[DatasetItem] =
    sql"UPDATE $TableName SET name = ${ seqSet.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name")
      .transact(xa)

  def find(id: DatasetId): IO[Option[DatasetItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[DatasetItem]
      .option
      .transact(xa)

  def delete(id: DatasetId): IO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(xa)
      .map(_ => Unit)
}

object DbDatasetRepo {

  val TableName = "datasets"

}
