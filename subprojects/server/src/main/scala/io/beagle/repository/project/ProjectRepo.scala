package io.beagle.repository.project

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Project, ProjectId, ProjectItem}

trait ProjectRepo {
  def create(project: Project): IO[ProjectItem]

  def update(id: ProjectId, project: Project): IO[ProjectItem]

  def find(id: ProjectId): IO[Option[ProjectItem]]

  def delete(id: ProjectId): IO[Unit]

}

object ProjectRepo {

  def inMemory =
    InMemProjectRepo(
      Ref.unsafe[IO, Map[ProjectId, ProjectItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )
}
