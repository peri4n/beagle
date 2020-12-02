package io.beagle.search

import cats.effect.IO
import io.beagle.exec.Exec


case class SearchSettings(protocol: String = "http", host: String = "localhost", port: Int = 9200, indexName: String = "fasta", exec: Exec) {
  def environment(): IO[SearchEnv] = IO {
    ElasticSearchEnv(protocol, host, port, indexName, exec)
  }
}
