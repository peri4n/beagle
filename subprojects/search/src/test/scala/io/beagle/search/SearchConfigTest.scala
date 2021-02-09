package io.beagle.search

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class SearchConfigTest extends AnyFunSpec with Matchers {

  describe("SearchSettings") {
    it("should be configurable via file") {
      ConfigSource.resources("search.conf").load[SearchConfig] should be(Right(SearchConfig(
        host = "remote-server",
        port = 9300,
        indexName = "sequences"
      )))
    }
  }
}
