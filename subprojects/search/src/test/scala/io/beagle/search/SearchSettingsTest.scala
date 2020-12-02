package io.beagle.search

import io.beagle.exec.Exec.Fixed
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class SearchSettingsTest extends AnyFunSpec with Matchers {

  describe("SearchSettings") {
    it("should be configurable via file") {
      ConfigSource.resources("search.conf").load[SearchSettings] should be(Right(SearchSettings(exec = Fixed(2))))
    }
    it("should environment be able to create an environment") {

    }
  }
}
