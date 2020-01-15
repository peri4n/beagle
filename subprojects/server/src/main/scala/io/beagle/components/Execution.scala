package io.beagle.components

import cats.data.Reader
import cats.effect.{ContextShift, IO, Timer}
import io.beagle.Env

import scala.concurrent.ExecutionContext.global

trait Execution {

  implicit val threadPool: ContextShift[IO]

  implicit val timer: Timer[IO]

}

object Execution {

  private val execution = Reader[Env, Execution](_.execution)

  val threadPool = execution map { _.threadPool }

  val timer = execution map { _.timer }

  case object GlobalExecution extends Execution {

    val threadPool: ContextShift[IO] = IO.contextShift(global)

    val timer: Timer[IO] = IO.timer(global)
  }
}
