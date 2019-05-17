package io.beagle

import com.typesafe.config.ConfigFactory

trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

}

trait ElasticSearchSettings {

  def protocol : String

  def host: String

  def port: Int

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

    def protocol = config.getString("elasticsearch.protocol")

    def host = config.getString("elasticsearch.host")

    def port = config.getInt("elasticsearch.port")

  }
}
