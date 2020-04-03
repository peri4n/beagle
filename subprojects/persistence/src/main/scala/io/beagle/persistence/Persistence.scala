package io.beagle.persistence

import cats.effect.IO
import cats.implicits._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor.Aux
import io.beagle.exec.Execution
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.FC
import doobie.util.transactor.Transactor

sealed trait Persistence {

  def host: String

  def port: Int

  def database: String

  def user: String

  def password: String

  def driverClass: String

  def createDb = {
    val create = (fr"CREATE DATABASE" ++ Fragment.const(database))
      .update
      .run
      .handleError(_ => 0)

    implicit val pool = execution.threadPool
    val xa = Transactor.fromDriverManager[IO](driverClass, s"jdbc:postgresql://$host:$port/postgres", user, password)

    (for {
      yes <- FC.setAutoCommit(true) *> create <* FC.setAutoCommit(false)
    } yield yes)
      .transact(xa)
  }

  def createTables: IO[Unit] = (for {
    _ <- UserService.createTable()
    _ <- ProjectService.createTable()
    _ <- DatasetService.createTable()
  } yield ())
    .transact(transactor)

  def execution: Execution

  lazy val transactor: Aux[IO, HikariDataSource] = {
    implicit val pool = execution.threadPool

    val config = new HikariConfig()
    config.setJdbcUrl(s"jdbc:postgresql://$host:$port/$database")
    config.setUsername(user)
    config.setPassword(password)
    config.setMaximumPoolSize(5)
    config.setDriverClassName(driverClass)

    HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)
  }
}

case class Postgres(database: String,
                    user: String,
                    password: String,
                    host: String = "localhost",
                    port: Int = 5432,
                    execution: Execution) extends Persistence {

  val driverClass: String = "org.postgresql.Driver"
}

object Postgres {
  val port = 5432
}


