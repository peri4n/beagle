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
        Web("subprojects/frontend/dist/",
          9000,
          Postgres("beagle", "fbull", "password", execution = Fixed(3)),
          Search(execution = Fixed(1)),
          Security("beagle", JwtConf(30 minutes, "secret")),
          execution = Fixed(4)))
    }
  }

}
