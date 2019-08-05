package io.beagle.repository.dataset

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Dataset, DatasetId, DatasetItem}

case object DbDatasetRepo extends DatasetRepo {

  val TableName = "datasets"

  def create(seqSet: Dataset): ConnectionIO[DatasetItem] =
    sql"INSERT INTO $TableName (name) VALUES (${ seqSet.name })".update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name")

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem] =
    sql"UPDATE $TableName SET name = ${ seqSet.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name")

  def find(id: DatasetId): ConnectionIO[Option[DatasetItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[DatasetItem]
      .option

  def delete(id: DatasetId): ConnectionIO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .map(_ => Unit)
}
