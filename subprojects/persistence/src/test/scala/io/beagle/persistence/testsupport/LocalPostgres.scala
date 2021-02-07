package io.beagle.persistence.testsupport

import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DB, PostgresConfig}
import org.scalatest.funspec.AnyFunSpec

trait LocalPostgres extends AnyFunSpec {

  def config: PostgresConfig = PostgresConfig(
    "beagle", "postgres", "", "localhost", 5432, Global()
  )

  lazy val environment: DB = config.environment().unsafeRunSync()

  lazy val xa = environment.transactor

}
