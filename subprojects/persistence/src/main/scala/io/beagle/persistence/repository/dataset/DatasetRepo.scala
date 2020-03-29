package io.beagle.persistence.repository.dataset

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Dataset, DatasetId, DatasetItem, ProjectId}
import io.beagle.persistence.metas._

case object DatasetRepo {

  def createTable(): ConnectionIO[Int] = {
    sql"""CREATE TABLE IF NOT EXISTS datasets (
         | id serial PRIMARY KEY,
         | name VARCHAR(255) UNIQUE NOT NULL,
         | owner_id INTEGER REFERENCES users(id),
         | project_id INTEGER REFERENCES projects(id),
         | created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
         | last_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)""".stripMargin.update.run
  }

  def create(dataset: Dataset): ConnectionIO[DatasetItem] =
    sql"""INSERT INTO datasets (name, owner_id, project_id, created, last_modified)
          VALUES (${ dataset.name }, ${dataset.ownerId}, ${ dataset.projectIds }, ${ dataset.created }, ${ dataset.lastModified })"""
      .update
      .withUniqueGeneratedKeys[DatasetItem]("id", "name", "owner_id", "project_id", "created", "last_modified")

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
