package io.beagle.repository.project

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.domain.{Project, ProjectId, ProjectItem}

class DbProjectRepo(xa: Transactor[IO]) extends ProjectRepo {

  import DbProjectRepo._

  def create(project: Project): IO[ProjectItem] =
    sql"INSERT INTO $TableName (username) VALUES (${ project.name })".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name")
      .transact(xa)

  def update(id: ProjectId, project: Project): IO[ProjectItem] =
    sql"UPDATE $TableName SET name = ${ project.name } WHERE id = $id".update
      .withUniqueGeneratedKeys[ProjectItem]("id", "name")
      .transact(xa)

  def find(id: ProjectId): IO[Option[ProjectItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[ProjectItem]
      .option
      .transact(xa)

  def delete(id: ProjectId): IO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(xa)
      .map(_ => Unit)
}

object DbProjectRepo {
  val TableName = "projects"
}
