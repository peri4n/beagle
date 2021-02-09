package io.beagle.components

import io.beagle.exec.Exec.Fixed
import io.beagle.persistence.{DbCredentials, PostgresConfig}
import io.beagle.search.ElasticSearchConfig
import io.beagle.security.{JwtSettings, SecuritySettings}
import io.beagle.web.WebSettings
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class WebSettingsTest extends AnyFunSpec with Matchers {

  describe("The web server") {
    it("can be configured via file") {
      ConfigSource.resources("web.conf").load[WebSettings] should be(
        Right(WebSettings(
          "subprojects/frontend/dist/",
          9000,
          PostgresConfig(credentials = DbCredentials("fbull", "password")),
          ElasticSearchConfig(),
          SecuritySettings(JwtSettings(30 minutes, "secret")), Fixed(4))))
    }
  }

}
