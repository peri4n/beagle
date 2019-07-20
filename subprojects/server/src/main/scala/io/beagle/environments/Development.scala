package io.beagle.environments

import com.typesafe.config.ConfigFactory
import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo


case class Development() extends Env {

  env =>

  override val settings = new Settings {

    private val config = ConfigFactory.load("development.conf")

    def uiRoot = config.getString("ui.root")

    val elasticSearch = new ElasticSearchSettings {

      val protocol = config.getString("elasticsearch.protocol")

      val host = config.getString("elasticsearch.host")

      val port = config.getInt("elasticsearch.port")

    }
  }

  val controllers = new Controllers {

    def seqset = Controllers.seqset.run(env)

    def upload = Controllers.upload.run(env)

    def health = Controllers.health.run(env)

    def search = Controllers.search.run(env)

    def static = Controllers.static.run(env)
  }

  val services = new Services {
    def elasticSearch = Services.elasticSearch.run(env)
  }

  def repositories = new Repositories {

    def sequenceSet = DatasetRepo.inMemory
  }
}
