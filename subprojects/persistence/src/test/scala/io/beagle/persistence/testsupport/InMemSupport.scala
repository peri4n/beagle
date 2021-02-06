package io.beagle.persistence.testsupport

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{InMemDB, PersistenceEnv}

trait InMemSupport {

  private lazy val environment: PersistenceEnv = InMemDB(Global()).environment().unsafeRunSync()

  val transactor: Transactor[IO] = environment.transactor

}
