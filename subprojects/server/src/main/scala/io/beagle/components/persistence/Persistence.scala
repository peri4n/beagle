package io.beagle.components.persistence

import java.sql.Connection

import cats.data.Reader
import cats.effect.{IO, Resource}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.free.KleisliInterpreter
import doobie.hikari.HikariTransactor
import doobie.implicits._
import cats.implicits._
import doobie.util.transactor.{Strategy, Transactor}
import io.beagle.Env
import io.beagle.components.Execution
import doobie.FC
import doobie.util.fragment.Fragment

trait Persistence {

  def transactor: Transactor.Aux[IO, HikariDataSource]

  def hook: IO[Unit]

}

object Persistence {

  private val persistence = Reader[Env, Persistence](_.persistence)

  val transactor = persistence map { _.transactor }

  case class InMemoryPersistence(execution: Execution) extends Persistence {

    def transactor: Transactor.Aux[IO, HikariDataSource] = {
      val connect = (_: HikariDataSource) => Resource.pure[IO, Connection](null)
      implicit val e = execution.threadPool
      val interp = KleisliInterpreter[IO](execution.blocker).ConnectionInterpreter
      Transactor(new HikariDataSource(), connect, interp, Strategy.void)
    }

    def hook: IO[Unit] = IO.unit
  }

  case class PostgresPersistence(database: String,
                                 username: String,
                                 password: String,
                                 host: String = "localhost",
                                 port: Int = 5432,
                                 protocol: String = "jdbc:postgresql",
                                 driver: String = "org.postgresql.Driver",
                                 execution: Execution) extends Persistence {

    lazy val transactor = {
      implicit val e = execution.threadPool

      val config = new HikariConfig()
      config.setJdbcUrl(s"jdbc:postgresql://$host:$port/${database.toLowerCase}")
      config.setUsername(username)
      config.setPassword(password)
      config.setMaximumPoolSize(5)

      HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)
    }

    def hook: IO[Unit] = {
      implicit val e = execution.threadPool
      def createDb = (fr"CREATE DATABASE" ++ Fragment.const(database))
        .update
        .run

      def createUserTable =
        sql"""CREATE TABLE IF NOT EXISTS users (
             | id serial PRIMARY KEY,
             | username VARCHAR(255) UNIQUE NOT NULL,
             | password VARCHAR(255),
             | email VARCHAR(255),
             | created TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)""".stripMargin.update.run

      (for {
        _ <- FC.setAutoCommit(true) *> createDb <* FC.setAutoCommit(false)
      } yield ())
        .transact(Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "fbull", ""))
    }

  }

  //  def instance =
  //    for {
  //      settings <- Env.settings
  //      execution <- Env.execution
  //    } yield settings.persistence match {
  //      case InMemory                      => InMemoryPersistence(execution)
  //      case LocalPostgre(db, _, user, pw) => PostgresPersistence(database = db, username = user, password = pw, execution = execution)
  //    }
}

