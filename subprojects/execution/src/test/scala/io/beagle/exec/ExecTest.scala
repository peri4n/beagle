package io.beagle.exec

import io.beagle.exec.Exec.{Fixed, Global}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ExecTest extends AnyFunSpec with Matchers {

  describe("A thread pool") {
    it("can be configured as a fixed thread pool.") {
      ConfigSource.resources("fixed.conf").load[Fixed] should be(Right(Fixed(2)))
    }

    it("can be configured as the global thread pool.") {
      ConfigSource.resources("global.conf").load[Global] should be(Right(Global()))
    }
  }
}
