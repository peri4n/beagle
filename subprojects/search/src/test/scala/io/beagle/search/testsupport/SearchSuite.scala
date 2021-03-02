package io.beagle.search.testsupport

import io.beagle.exec.Exec.Global
import io.beagle.search.{ElasticSearch, ElasticSearchConfig}
import munit.CatsEffectSuite
import org.testcontainers.elasticsearch.ElasticsearchContainer

trait SearchSuite extends CatsEffectSuite {

  override def munitFixtures = List(setup)

  val setup = new Fixture[ElasticSearch]("elastic-search") {

    val search = {
      val container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.3.0")
      container.start()

      val Array(host, port) = container.getHttpHostAddress.split(":")
      ElasticSearchConfig("http", host, port.toInt).environment(Global)
    }

    override def apply(): ElasticSearch = search

    override def beforeAll = {
      search.searchService.createSequenceIndex().unsafeRunSync()
    }

    override def afterEach(context: AfterEach): Unit = {
      search.searchService.deleteAll().unsafeRunSync()
    }
  }

}
