package io.beagle.components

import cats.effect.IO
import doobie.util.transactor.Transactor

trait Transaction {

  def transactor: Transactor.Aux[IO, Unit]

}

