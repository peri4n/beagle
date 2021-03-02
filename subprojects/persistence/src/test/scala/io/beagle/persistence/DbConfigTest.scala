package io.beagle.persistence

import munit.FunSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class DbConfigTest extends FunSuite {

  test("can be configured to use PostgreSQL") {
    assertEquals(
      ConfigSource.resources("postgres.conf").load[DbConfig],
      Right(PgConfig("remote-server", 1234, "dbName", DbCredentials("user", "password"), 3)))
  }

  test("can be configured to use in-memory abstractions") {
    assertEquals(
      ConfigSource.resources("inmem.conf").load[DbConfig],
      Right(InMemConfig))
  }
}
