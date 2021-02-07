package io.beagle.persistence.testsupport

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DB, InMemConfig}

trait InMemSupport {

  private lazy val environment: DB = InMemConfig(Global()).environment().unsafeRunSync()

  val transactor: Transactor[IO] = environment.transactor

}
