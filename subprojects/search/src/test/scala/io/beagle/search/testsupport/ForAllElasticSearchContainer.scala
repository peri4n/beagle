package io.beagle.search.testsupport

import com.dimafeng.testcontainers.{ElasticsearchContainer, ForAllTestContainer}
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import org.scalatest.funspec.AnyFunSpec

trait ForAllElasticSearchContainer extends AnyFunSpec with ForAllTestContainer {

  override val container = {
    val pg = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.3.0")
    pg.start()
    pg
  }

  lazy val client = {
    ElasticClient(JavaClient(ElasticProperties(s"http://${container.httpHostAddress}/fasta")))
  }

}
