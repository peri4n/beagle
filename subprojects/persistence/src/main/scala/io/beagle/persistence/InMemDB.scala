package io.beagle.persistence

import cats.effect.{Blocker, IO, Resource}
import doobie.KleisliInterpreter
import doobie.util.transactor.{Strategy, Transactor}
import io.beagle.exec.Exec
import io.beagle.exec.Exec.Global
import io.beagle.persistence.repository.dataset.InMemDatasetRepo
import io.beagle.persistence.repository.project.InMemProjectRepo
import io.beagle.persistence.repository.user.InMemUserRepo
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}

import java.sql.Connection

case class InMemDB(execution: Exec, userRepo: InMemUserRepo, projectRepo: InMemProjectRepo, datasetRepo: InMemDatasetRepo) extends DB {

  implicit val pool = execution.shift

  lazy val xa = Transactor(
    (),
    (_: Unit) => Resource.pure[IO, Connection](null),
    KleisliInterpreter[IO](Blocker.liftExecutionContext(execution.context)).ConnectionInterpreter,
    Strategy.void
  )

  lazy val userService: UserService = UserService(userRepo)

  lazy val projectService: ProjectService = ProjectService(userService, projectRepo)

  lazy val datasetService: DatasetService = DatasetService(datasetRepo)

  def initSchema(): IO[Unit] = IO.unit

}

object InMemDB {
  def create(): IO[InMemDB] = {
    for {
      userRepo <- InMemUserRepo.create()
      projectRepo <- InMemProjectRepo.create()
      datasetRepo <- InMemDatasetRepo.create()
    } yield InMemDB(Global, userRepo, projectRepo, datasetRepo)
  }
}
