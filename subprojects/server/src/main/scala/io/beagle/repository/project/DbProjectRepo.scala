package io.beagle.repository.project

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{Project, ProjectId, ProjectItem}

case object DbProjectRepo extends ProjectRepo {

  import ProjectItem._
  val TableName = "projects"

  def create(project: Project): ConnectionIO[ProjectItem] =
    sql"INSERT INTO $TableName (username) VALUES (${ project.name })".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name")

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem] =
    sql"UPDATE $TableName SET name = ${ project.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name")

  def find(id: ProjectId): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[ProjectItem]
      .option

  def findByName(name: String): ConnectionIO[Option[ProjectItem]] =
    sql"SELECT * FROM $TableName WHERE name = $name".query[ProjectItem]
      .option

  def delete(id: ProjectId): ConnectionIO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .map(_ => ())
}
