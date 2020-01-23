package io.beagle.components

import cats.data.Reader
import cats.effect.{Blocker, ContextShift, IO, Timer}
import io.beagle.Env

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

trait Execution {

  val context: ExecutionContext

  def threadPool: ContextShift[IO] = IO.contextShift(context)

  def timer: Timer[IO] = IO.timer(context)

  def blocker = Blocker.liftExecutionContext(context)

}

object Execution {

  private val execution = Reader[Env, Execution](_.execution)

  val threadPool = execution map { _.threadPool }

  val timer = execution map { _.timer }

  case object GlobalExecution extends Execution {

    val context: ExecutionContext = global

  }
}
