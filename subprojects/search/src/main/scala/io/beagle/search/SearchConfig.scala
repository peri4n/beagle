package io.beagle.search

import cats.effect.IO
import io.beagle.exec.Exec


case class SearchConfig(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String = "fasta") {
  def environment(exec: Exec): IO[Search] = IO {
    ElasticSearch(protocol, host, port, indexName, exec)
  }
}
