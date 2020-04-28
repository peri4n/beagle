package io.beagle.search.testsupport

import com.dimafeng.testcontainers.{ElasticsearchContainer, ForAllTestContainer}
import io.beagle.exec.Execution.Global
import io.beagle.search.Search
import org.scalatest.funspec.AnyFunSpec

trait SearchSupport extends AnyFunSpec with ForAllTestContainer {

  override val container = {
    val es = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.3.0")
    es.start()
    es
  }

  lazy val search = {
    val Array(host, port) = container.httpHostAddress.split(":")
    Search("http", host, port.toInt, "fasta", Global)
  }

  lazy val client = search.client

  lazy val service = search.service

}
