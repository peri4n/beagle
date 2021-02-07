package io.beagle.persistence

import cats.effect.{Blocker, IO, Resource}
import doobie.KleisliInterpreter
import doobie.util.transactor.{Strategy, Transactor}
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.InMemDatasetRepo
import io.beagle.persistence.repository.project.InMemProjectRepo
import io.beagle.persistence.repository.user.InMemUserRepo
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}

import java.sql.Connection

case class InMemDB(execution: Exec, userRepo: InMemUserRepo, projectRepo: InMemProjectRepo, datasetRepo: InMemDatasetRepo) extends DB {

  override def initSchema(): IO[Unit] = IO.unit

  override lazy val transactor: Transactor[IO] = {
    implicit val pool = execution.shift

    Transactor(
      (),
      (_: Unit) => Resource.pure[IO, Connection](null),
      KleisliInterpreter[IO](Blocker.liftExecutionContext(execution.context)).ConnectionInterpreter,
      Strategy.void
    )
  }

  override lazy val userService: UserService = UserService(userRepo)

  override lazy val projectService: ProjectService = ProjectService(userService, projectRepo)

  override lazy val datasetService: DatasetService = DatasetService(datasetRepo)

}
