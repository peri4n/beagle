package io.beagle.exec

import java.util.concurrent.Executors

import cats.effect.{Blocker, ContextShift, IO, Timer}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.{global => glob}

sealed trait Exec {

  def context: ExecutionContext

  implicit val shift: ContextShift[IO] = IO.contextShift(context)

  def timer: Timer[IO] = IO.timer(context)

  def blocker = Blocker.liftExecutionContext(context)

}

object Exec {

  final case class Global() extends Exec {
    lazy val context: ExecutionContext = glob
  }

  final case class Fixed(threads: Int) extends Exec {
    lazy val context = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threads))
  }

}

