package io.beagle.persistence.repository.project

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Project, ProjectId, ProjectItem, UserId}

case class InMemProjectRepo(db: Ref[IO, Map[ProjectId, ProjectItem]], counter: Ref[IO, Long]) extends ProjectRepository {

  override def createTable(): ConnectionIO[Int] = ???

  override def create(project: Project): ConnectionIO[ProjectItem] = ???

  override def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem] = ???

  override def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] = ???

  override def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]] = ???

  override def delete(id: ProjectId): ConnectionIO[Unit] = ???

  override def deleteAll(): ConnectionIO[Unit] = ???
}

object InMemProjectRepo {

  def create(): IO[InMemProjectRepo] =
    for {
      db <- Ref.of[IO, Map[ProjectId, ProjectItem]](Map.empty)
      counter <- Ref.of[IO, Long](1L)
    } yield new InMemProjectRepo(db, counter)

}
