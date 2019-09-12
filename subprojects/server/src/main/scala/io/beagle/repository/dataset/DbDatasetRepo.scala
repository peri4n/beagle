package io.beagle.repository.dataset

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}
import io.beagle.domain.metas._

case object DbDatasetRepo extends DatasetRepo {

  def create(dataset: Dataset): ConnectionIO[DatasetItem] =
    sql"""INSERT INTO datasets (name, project_id, created, last_modified)
          VALUES (${ dataset.name }, ${ dataset.projectId }, ${ dataset.created }, ${ dataset.lastModified })"""
      .update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name", "project_id", "created", "last_modified")

  def update(id: DatasetId, seqSet: Dataset): ConnectionIO[DatasetItem] =
    sql"UPDATE datasets SET name = ${ seqSet.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name")

  def findById(id: DatasetId): ConnectionIO[Option[DatasetItem]] =
    sql"SELECT * FROM datasets WHERE id = $id".query[DatasetItem]
      .option

  def findByName(name: String, project: ProjectId): ConnectionIO[Option[DatasetItem]] =
    sql"SELECT * FROM datasets WHERE name = $name AND project_id = $project".query[DatasetItem]
      .option

  def delete(id: DatasetId): ConnectionIO[Unit] =
    sql"DELETE FROM datasets WHERE id = $id".update.run
      .map(_ => ())

  def deleteAll(): ConnectionIO[Unit] =
    sql"DELETE FROM datasets".update
      .run
      .map(_ => ())
}
