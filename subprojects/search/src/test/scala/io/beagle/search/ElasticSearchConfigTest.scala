package io.beagle.search

import munit.FunSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ElasticSearchConfigTest extends FunSuite {

  test("should be configurable via file") {
    assertEquals(
      ConfigSource.resources("search.conf").load[ElasticSearchConfig],
      Right(ElasticSearchConfig(
        host = "remote-server",
        port = 9300,
        indexName = "sequences"
      )))
  }
}
