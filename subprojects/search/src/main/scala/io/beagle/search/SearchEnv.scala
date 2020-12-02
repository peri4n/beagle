package io.beagle.search

import io.beagle.exec.Exec

trait SearchEnv {

  def searchService: SearchService

}

case class ElasticSearchEnv(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String, execution: Exec) extends SearchEnv {


  lazy val searchService = SearchService(protocol, host, port, indexName, execution)
}

case class InMemSearchEnv() extends SearchEnv {
  override def searchService: SearchService = throw new NotImplementedError("Should later be used for testing")
}
