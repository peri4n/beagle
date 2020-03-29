package io.beagle.security

import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration._

class SecurityTest extends AnyFunSpec with Matchers with EitherValues {

  describe("The security settings") {
    it("can be configured via file") {
      ConfigSource.resources("security.conf").load[Security].right.value should be(Security("testRealm", JwtConf(5 seconds, "foo")))
    }
  }

}
