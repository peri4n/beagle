package io.beagle.persistence

import java.sql.Connection

import cats.effect.{Blocker, IO, Resource}
import cats.implicits._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor.Aux
import doobie.util.transactor.{Strategy, Transactor}
import doobie.{FC, KleisliInterpreter}
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.{DatasetRepo, InMemDatasetRepo}
import io.beagle.persistence.repository.project.{InMemProjectRepo, ProjectRepo}
import io.beagle.persistence.repository.user.{InMemUserRepo, UserRepo}
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}

sealed trait PersistenceEnv {

  def execution: Exec

  def createDb(): IO[Int]

  def createTables(): IO[Unit]

  def userService: UserService

  def projectService: ProjectService

  def datasetService: DatasetService

  def transactor: Transactor[IO]

}

case class PostgresEnv(database: String,
                       user: String,
                       password: String,
                       host: String = "localhost",
                       port: Int = 5432,
                       execution: Exec) extends PersistenceEnv {

  val driverClass: String = "org.postgresql.Driver"

  override def createDb() = {
    val create = (fr"CREATE DATABASE" ++ Fragment.const(database))
      .update
      .run
      .handleError(_ => 0)

    implicit val pool = execution.shift
    val xa = Transactor.fromDriverManager[IO](driverClass, s"jdbc:postgresql://$host:$port/postgres", user, password)

    (for {
      yes <- FC.setAutoCommit(true) *> create <* FC.setAutoCommit(false)
    } yield yes)
      .transact(xa)
  }

  lazy val transactor: Aux[IO, HikariDataSource] = {
    implicit val pool = execution.shift

    val config = new HikariConfig()
    config.setJdbcUrl(s"jdbc:postgresql://$host:$port/$database")
    config.setUsername(user)
    config.setPassword(password)
    config.setMaximumPoolSize(5)
    config.setDriverClassName(driverClass)

    HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)
  }

  override def createTables(): IO[Unit] = (for {
    _ <- userService.createTable()
    _ <- projectService.createTable()
    _ <- datasetService.createTable()
  } yield ())
    .transact(transactor)

  override lazy val userService: UserService = UserService(UserRepo)

  override lazy val projectService: ProjectService = ProjectService(userService, ProjectRepo)

  override lazy val datasetService: DatasetService = DatasetService(DatasetRepo)

}

case class InMemEnv(execution: Exec, userRepo: InMemUserRepo, projectRepo: InMemProjectRepo, datasetRepo: InMemDatasetRepo) extends PersistenceEnv {

  override def createDb(): IO[Int] = IO { 1 }

  override def createTables(): IO[Unit] = IO.unit

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
