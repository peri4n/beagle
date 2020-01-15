package io.beagle.components.persistence

import java.sql.Connection

import cats.data.Reader
import cats.effect.{Blocker, IO, Resource}
import doobie.free.KleisliInterpreter
import doobie.util.transactor.{Strategy, Transactor}
import io.beagle.Env
import io.beagle.components.Execution

import scala.concurrent.ExecutionContext.Implicits.global

trait Persistence {

  def transactor: Transactor.Aux[IO, Unit]

}

object Persistence {

  private val persistence = Reader[Env, Persistence](_.persistence)

  val transactor = persistence map { _.transactor }

  case class InMemoryPersistence(execution: Execution) extends Persistence {

    import execution._

    def transactor: Transactor.Aux[IO, Unit] = {
      val connect = (_: Unit) => Resource.pure[IO, Connection](null)
      val interp = KleisliInterpreter[IO](Blocker.liftExecutionContext(global)).ConnectionInterpreter
      Transactor((), connect, interp, Strategy.void)
    }
  }

  case class PostgresPersistence(database: String, username: String, password: String, host: String = "localhost", protocol: String = "jdbc:postgresql", driver: String = "org.postgresql.Driver", execution: Execution) extends Persistence {

    lazy val transactor = {
      import execution._

      Transactor.fromDriverManager[IO](
        driver = driver,
        url = s"$protocol://$host/$database",
        user = username,
        pass = password
      )
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

