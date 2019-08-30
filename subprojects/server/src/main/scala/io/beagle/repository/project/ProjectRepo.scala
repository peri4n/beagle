package io.beagle.repository.project

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Project, ProjectId, ProjectItem, UserId}

trait ProjectRepo {

  def create(project: Project): ConnectionIO[ProjectItem]

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem]

  def find(id: ProjectId): ConnectionIO[Option[ProjectItem]]

  def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]]

  def delete(id: ProjectId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}

object ProjectRepo {

  def inMemory =
    InMemProjectRepo(
      Ref.unsafe[IO, Map[ProjectId, ProjectItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )

  def inDB = DbProjectRepo
}
