package io.beagle.repository.project

import cats.effect.concurrent.Ref
import cats.effect.{Async, IO}
import doobie.free.connection.ConnectionIO
import io.beagle.domain._

case class InMemProjectRepo(db: Ref[IO, Map[ProjectId, ProjectItem]], counter: Ref[IO, Long]) extends ProjectRepo {

  def create(project: Project): ConnectionIO[ProjectItem] =
    Async[ConnectionIO].liftIO(
      for {
        c <- counter.get
        _ <- counter.update(_ + 1)
        id = ProjectId(c)
        view = ProjectItem(id, project)
        _ <- db.update(store => store + ( id -> view ))
      } yield view)

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem] =
    Async[ConnectionIO].liftIO(
      db.modify(map => {
        val view = ProjectItem(id, project)
        (map.updated(id, view), view)
      }))

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]] =
    Async[ConnectionIO].liftIO(db.get.map(_.get(id)))

  def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]] =
    Async[ConnectionIO].liftIO(db.get.map(_.values.find(item =>
      item.project.name == name && item.project.ownerId == owner))
    )

  def delete(id: ProjectId): ConnectionIO[Unit] =
    Async[ConnectionIO].liftIO(db.update(map => map - id))

  def deleteAll(): ConnectionIO[Unit] =
    Async[ConnectionIO].liftIO(db.update(_ => Map.empty))
}
