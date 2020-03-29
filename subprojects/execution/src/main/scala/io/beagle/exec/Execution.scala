package io.beagle.exec

import java.util.concurrent.Executors

import cats.effect.{Blocker, ContextShift, IO, Timer}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.{global => glob}

sealed trait Execution {

  def context: ExecutionContext

  def threadPool: ContextShift[IO] = IO.contextShift(context)

  def timer: Timer[IO] = IO.timer(context)

  def blocker = Blocker.liftExecutionContext(context)

}

object Execution {

  final case object Global extends Execution {
    val context: ExecutionContext = glob
  }

  final case class Fixed(threads: Int) extends Execution {
    val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threads))
  }

}

