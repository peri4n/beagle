package io.beagle.search

import io.beagle.exec.Exec

case class ElasticSearchConfig(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String = "fasta") {

  def connectionUrl() = s"${ protocol }://$host:$port/"

  def environment(exec: Exec) = ElasticSearch(this, exec)
}
