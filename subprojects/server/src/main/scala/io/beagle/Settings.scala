package io.beagle

import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}

trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

}

trait ElasticSearchSettings {

  def protocol: String

  def host: String

  def port: Int

  val client = ElasticClient(ElasticProperties(s"${ protocol }://${ host }:${ port }"))

}

object Settings {

  import Env.settings

  val elasticSearch = settings map {
    _.elasticSearch
  }

}

