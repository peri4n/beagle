package io.beagle.search.testsupport

import com.dimafeng.testcontainers.ElasticsearchContainer
import io.beagle.exec.Exec.Global
import io.beagle.search.ElasticSearch
import org.scalatest.funspec.AnyFunSpec

trait SearchSupport extends AnyFunSpec {

  lazy val container = ElasticsearchContainer.Def("docker.elastic.co/elasticsearch/elasticsearch:7.3.0").start()

  lazy val environment = {
    val Array(host, port) = container.httpHostAddress.split(":")
    ElasticSearch("http", host, port.toInt, "fasta", Global())
  }

  lazy val service = environment.searchService

}
