package io.beagle.components

import cats.data.Reader
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import io.beagle.Env
import io.beagle.components.settings.SearchSettings
import io.beagle.service.SearchService

trait Search {

  def client: ElasticClient

  def service: SearchService

  def settings: SearchSettings

}

object Search {

  private val search = Reader[Env, Search](_.search)

  lazy val client = search map { _.client }

  lazy val service = search map { _.service }

  lazy val settings = search map { _.settings }

  case class DefaultSearch(env: Env) extends Search {

    lazy val settings: SearchSettings = SearchSettings.Local()

    lazy val client: ElasticClient = ElasticClient(JavaClient(ElasticProperties(s"${ settings.protocol }://${ settings.host }:${ settings.port }")))

    lazy val service = SearchService.instance(env)


  }

}
