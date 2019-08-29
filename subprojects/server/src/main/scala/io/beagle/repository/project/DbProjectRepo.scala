package io.beagle.repository.project

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Project, ProjectId, ProjectItem, UserId}
import io.beagle.domain.metas._

case object DbProjectRepo extends ProjectRepo {

  def create(project: Project): ConnectionIO[ProjectItem] =
    sql"""INSERT INTO projects (name, owner, created)
        VALUES (${ project.name }, ${ project.ownerId }, ${ project.created })"""
      .update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name", "owner", "created")

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem] =
    sql"UPDATE projects SET name = ${ project.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name", "owner", "created")

  def find(id: ProjectId): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM projects WHERE id = $id".query[ProjectItem]
      .option

  def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM projects WHERE name = $name AND owner = $owner".query[ProjectItem]
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
