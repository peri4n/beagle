package io.beagle.security

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class SecuritySettingsTest extends AnyFunSpec with Matchers {

  describe("The security settings") {
    it("can be configured via file") {
      ConfigSource.resources("security.conf").load[SecuritySettings] should be(Right(SecuritySettings(JwtSettings(5 seconds, "foo"))))
    }
  }

}
