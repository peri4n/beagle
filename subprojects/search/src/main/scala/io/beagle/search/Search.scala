package io.beagle.search

import io.beagle.exec.Exec

trait Search {

  def searchService: SearchService

}

case class ElasticSearch(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String, execution: Exec) extends Search {
  lazy val searchService = SearchService(protocol, host, port, indexName, execution)
}

