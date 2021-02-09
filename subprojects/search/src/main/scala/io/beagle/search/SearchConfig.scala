package io.beagle.search

import io.beagle.exec.Exec

case class SearchConfig(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String = "fasta") {
  def environment(exec: Exec) = ElasticSearch(protocol, host, port, indexName, exec)
}
