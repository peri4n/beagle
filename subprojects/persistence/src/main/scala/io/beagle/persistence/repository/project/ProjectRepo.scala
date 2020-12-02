package io.beagle.persistence.repository.project

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Project, ProjectId, ProjectItem, UserId}
import doobie.implicits.javatime._

case object ProjectRepo extends ProjectRepository {
  def createTable(): ConnectionIO[Int] = {
    sql"""CREATE TABLE IF NOT EXISTS projects (
         | id serial PRIMARY KEY,
         | name VARCHAR(255) UNIQUE NOT NULL,
         | owner_id INTEGER REFERENCES users(id),
         | created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)""".stripMargin.update.run
  }

  def create(project: Project): ConnectionIO[ProjectItem] =
    sql"""INSERT INTO projects (name, owner_id, created)
        VALUES (${ project.name }, ${ project.ownerId }, ${ project.created })"""
      .update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name", "owner_id", "created")

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem] =
    sql"UPDATE projects SET name = ${ project.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name", "owner_id", "created")

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM projects WHERE id = $id".query[ProjectItem]
      .option

  def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM projects WHERE name = $name AND owner_id = $owner".query[ProjectItem]
      .option

  def delete(id: ProjectId): ConnectionIO[Unit] =
    sql"DELETE FROM projects WHERE id = $id".update
      .run
      .map(_ => ())

  def deleteAll(): ConnectionIO[Unit] =
    sql"DELETE FROM projects".update
      .run
      .map(_ => ())
}
