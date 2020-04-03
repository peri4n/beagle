package io.beagle.components

import io.beagle.exec.Execution.{Fixed, Global}
import io.beagle.persistence.Postgres
import io.beagle.search.Search
import io.beagle.security.{JwtConf, Security}
import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class WebTest extends AnyFunSpec with Matchers with EitherValues {

  describe("The web server") {
    it("can be configured via file") {
      val value1 = ConfigSource.resources("web.conf").load[Web]
      value1.right.value should be(
        Web("./dist",
          8088,
          Postgres("dbName", "username", "password", execution = Fixed(2)),
          Search(execution = Global),
          Security("realm", JwtConf(5 seconds, "secret")),
          execution = Fixed(3)))
    }
  }

}
