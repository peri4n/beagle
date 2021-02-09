package io.beagle.persistence

import io.beagle.exec.Exec.Fixed
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class PostgresConfigTest extends AnyFunSpec with Matchers with OptionValues {

  describe("The persistence layer") {
    it("can be configured to use PostgreSQL") {
      ConfigSource.resources("postgres.conf").load[PostgresConfig] should be(
        Right(PostgresConfig("remote-server", 1234, "dbName", DbCredentials("user", "password"), 3)))
    }
  }

}
