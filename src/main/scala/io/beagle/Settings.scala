package io.beagle

import com.typesafe.config.ConfigFactory

trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

}

trait ElasticSearchSettings {

  def elasticSearchHost: String

  def elasticSearchPort: Int

}

object Settings {

  import Env.settings

  val elasticSearch = settings map {
    _.elasticSearch
  }

}

object ProdSettings extends Settings {

  private val config = ConfigFactory.load()

  def uiRoot = config.getString("ui.root")

  def elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

    def elasticSearchHost = config.getString("elasticsearch.host")

    def elasticSearchPort = config.getInt("elasticsearch.port")

  }
}
