package io.beagle.search

import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.http.JavaClient
import io.beagle.exec.Execution

case class Search(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String = "fasta", execution: Execution) {

  lazy val client = ElasticClient(JavaClient(ElasticProperties(s"$protocol://$host:$port/")))

  lazy val service = SearchService(execution, indexName, client)

}
