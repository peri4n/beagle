package io.beagle.security

import munit.FunSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class SecuritySettingsTest extends FunSuite {

  test("can be configured via file") {
    assertEquals(
      ConfigSource.resources("security.conf").load[SecuritySettings],
      Right(SecuritySettings(JwtSettings(5 seconds, "foo"))))
  }

}
