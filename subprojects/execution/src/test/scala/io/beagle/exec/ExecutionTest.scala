package io.beagle.exec

import io.beagle.exec.Execution.Fixed
import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ExecutionTest extends AnyFunSpec with EitherValues with Matchers {

  describe("A fixed thread pool") {
    it("can be configured via file.") {
      ConfigSource.resources("fixed.conf").load[Fixed].right.value should be(Fixed(2))
    }
  }
}
