package io.beagle.environments.execution

import cats.effect.{ContextShift, IO, Timer}
import io.beagle.components.Execution

import scala.concurrent.ExecutionContext.global

case object GlobalExecution extends Execution {

  val threadPool: ContextShift[IO] = IO.contextShift(global)

  val timer: Timer[IO] = IO.timer(global)
}
