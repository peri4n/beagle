package io.beagle.repository.project

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain._

class InMemProjectRepo(db: Ref[IO, Map[ProjectId, ProjectItem]], counter: Ref[IO, Long]) extends ProjectRepo {
  def create(project: Project): IO[ProjectItem] =
  for {
    c <- counter.get
    _ <- counter.update(_ + 1)
    id = ProjectId(c)
    view = ProjectItem(id, project)
    _ <- db.update(store => store + ( id -> view ))
  } yield view

  def update(id: ProjectId, project: Project): IO[ProjectItem] =
  db.modify(map => {
    val view = ProjectItem(id, project)
    (map.updated(id, view), view)
  })

  def find(id: ProjectId): IO[Option[ProjectItem]] = db.get.map( _.get(id))

  def delete(id: ProjectId): IO[Unit] = db.update(map => map - id)
}
