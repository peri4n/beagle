package io.beagle.exec

import io.beagle.exec.Execution.{Fixed, Global}
import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ExecutionTest extends AnyFunSpec with EitherValues with Matchers {

  describe("A thread pool") {
    it("can be configured as a fixed thread pool.") {
      ConfigSource.resources("fixed.conf").load[Fixed].right.value should be(Fixed(2))
    }

    it("can be configured as the global thread pool.") {
      ConfigSource.resources("global.conf").load[Global].right.value should be(Global())
    }
  }
}
