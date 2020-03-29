package io.beagle.search

import io.beagle.exec.Execution.Fixed
import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class SearchTest extends AnyFunSpec with Matchers with EitherValues {

  describe("The search") {
    it("can be configured via file") {
      ConfigSource.resources("search.conf").load[Search].right.value should be(Search(execution = Fixed(2)))
    }
  }

}
