package io.beagle.environments

import com.typesafe.config.ConfigFactory
import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo


case object Development extends Env {

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

    def seqset = Controllers.dataset(env)

    def upload = Controllers.upload(env)

    def health = Controllers.health(env)

    def search = Controllers.search(env)

    def static = Controllers.static(env)
  }

  val services = new Services {
    val elasticSearch = Services.elasticSearch(env)
  }

  def repositories = new Repositories {

    def sequenceSet = DatasetRepo.inMemory
  }
}
