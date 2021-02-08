package io.beagle.persistence

import io.beagle.exec.Exec.Fixed
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class DbConfigTest extends AnyFunSpec with Matchers with OptionValues {

  describe("The persistence layer") {
    it("can be configured to use PostgreSQL") {
      ConfigSource.resources("postgres.conf").load[PostgresConfig] should be(Right(PostgresConfig("dbName", "fbull", "password", exec = Fixed(3))))
    }
  }

}
