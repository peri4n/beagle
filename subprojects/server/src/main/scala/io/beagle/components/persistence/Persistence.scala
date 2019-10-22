package io.beagle.components.persistence

import cats.effect.IO
import doobie.util.transactor.Transactor

trait Persistence {

  def transactor: Transactor.Aux[IO, Unit]

}

