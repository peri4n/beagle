package io.beagle.components

import io.beagle.exec.Exec.Fixed
import io.beagle.persistence.PostgresConfig
import io.beagle.search.SearchSettings
import io.beagle.security.{JwtSettings, SecuritySettings}
import io.beagle.web.WebSettings
import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class WebSettingsTest extends AnyFunSpec with Matchers with EitherValues {

  describe("The web server") {
    it("can be configured via file") {
      val actual = ConfigSource.resources("web.conf").load[WebSettings]
      val expected = WebSettings("subprojects/frontend/dist/", 9000, PostgresConfig("beagle", "fbull", "password", exec = Fixed(3)), SearchSettings(exec = Fixed(1)), SecuritySettings(JwtSettings(30 minutes, "secret")), Fixed(4))
      actual.right.value should be(expected)
    }
  }

}
