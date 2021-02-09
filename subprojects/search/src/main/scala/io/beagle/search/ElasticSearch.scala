package io.beagle.search

import io.beagle.exec.Exec

case class ElasticSearch(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String, execution: Exec) {
  lazy val searchService = SearchService(protocol, host, port, indexName, execution)
}

