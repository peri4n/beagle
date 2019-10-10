package io.beagle.components

import cats.effect.{ContextShift, IO, Timer}

trait Execution {

  implicit val threadPool: ContextShift[IO]

  implicit val timer: Timer[IO]

}
