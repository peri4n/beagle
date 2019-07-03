package io.beagle.environments

import com.typesafe.config.ConfigFactory
import io.beagle.components._
import io.beagle.repository.SequenceSetRepo

object Production extends Env {

  env =>

  override val settings: Settings = new Settings {

    private val config = ConfigFactory.load("production.conf")

    def uiRoot = config.getString("ui.root")

    val elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      val protocol = config.getString("elasticsearch.protocol")

      val host = config.getString("elasticsearch.host")

      val port = config.getInt("elasticsearch.port")

    }
  }

  val controllers = new Controllers {

    def seqset = Controllers.seqset(env)

    def upload = Controllers.upload(env)

    def health = Controllers.health(env)

    def search = Controllers.search(env)

    def static = ??? // the UI is served by a dedicated web server
  }

  def services: Services = new Services {
    def elasticSearch = Services.elasticSearch(env)
  }

  def repositories: Repositories = new Repositories {

    def sequenceSet: SequenceSetRepo = Repositories.sequenceSet(env)
  }

}
