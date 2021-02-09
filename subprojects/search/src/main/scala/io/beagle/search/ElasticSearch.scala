package io.beagle.search

import io.beagle.exec.Exec

case class ElasticSearch(config: ElasticSearchConfig, exec: Exec) {
  lazy val searchService = SearchService(config, exec)
}

