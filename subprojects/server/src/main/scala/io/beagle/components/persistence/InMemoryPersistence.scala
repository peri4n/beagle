package io.beagle.components.persistence

import java.sql.Connection

import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect.{Blocker, IO, Resource}
import doobie.free.KleisliInterpreter
import doobie.util.transactor.{Strategy, Transactor}
import io.beagle.components.Execution

case class InMemoryPersistence(execution: Execution) extends Persistence {

  import execution._

  def transactor: Transactor.Aux[IO, Unit] = {
    val connect = (_: Unit) => Resource.pure[IO, Connection](null)
    val interp = KleisliInterpreter[IO](Blocker.liftExecutionContext(global)).ConnectionInterpreter
    Transactor((), connect, interp, Strategy.void)
  }

}
