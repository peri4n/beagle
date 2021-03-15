package io.beagle.components

import io.beagle.exec.Exec.Fixed
import io.beagle.persistence.{DbCredentials, PgConfig}
import io.beagle.search.ElasticSearchConfig
import io.beagle.security.{JwtSettings, SecuritySettings}
import io.beagle.web.server
import io.beagle.web.server.{WebSettings, controller}
import munit.FunSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class WebSettingsTest extends FunSuite {

  test("can be configured via file") {
    assertEquals(
      ConfigSource.resources("web.conf").load[WebSettings],
      Right(server.WebSettings(
        "subprojects/frontend/dist/",
        9000,
        PgConfig(credentials = DbCredentials("fbull", "password")),
        ElasticSearchConfig(),
        SecuritySettings(JwtSettings(30 minutes, "secret")), Fixed(4))))
  }

}
