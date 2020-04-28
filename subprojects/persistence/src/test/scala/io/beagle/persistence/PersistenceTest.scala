package io.beagle.persistence

import io.beagle.exec.Execution.Fixed
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class PersistenceTest extends AnyFunSpec with Matchers with OptionValues {

  describe("The Postgres persistence") {
    it("can be configured via file") {
      ConfigSource.resources("postgres.conf").load[Postgres].toOption.value should be(Postgres("dbName", "fbull", "password", execution = Fixed(3)))
    }
  }

}