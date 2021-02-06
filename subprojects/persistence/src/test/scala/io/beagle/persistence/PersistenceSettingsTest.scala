package io.beagle.persistence

import io.beagle.exec.Exec.Fixed
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class PersistenceSettingsTest extends AnyFunSpec with Matchers with OptionValues {

  describe("The persistence layer") {
    it("can be configured to use PostgreSQL") {
      ConfigSource.resources("postgres.conf").load[Postgres] should be(Right(Postgres("dbName", "fbull", "password", exec = Fixed(3))))
    }

    it("can be configured to use in-memory abstractions") {
      ConfigSource.resources("inmem.conf").load[InMemDB] should be(Right(InMemDB(exec = Fixed(2))))
    }
  }

}
