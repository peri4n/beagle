package io.beagle.environments

import cats.effect.IO
import cats.implicits._
import com.typesafe.config.ConfigFactory
import io.beagle.components.{Controllers, ElasticSearchSettings, Env, Services, Settings}
import io.beagle.service.ElasticSearchService
import org.http4s.HttpRoutes

object Development extends Env {

  env =>

  override val settings: Settings = new Settings {

    private val config = ConfigFactory.load("development.conf")

    def uiRoot = config.getString("ui.root")

    val elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      val protocol = config.getString("elasticsearch.protocol")

      val host = config.getString("elasticsearch.host")

      val port = config.getInt("elasticsearch.port")

    }
  }

  val controllers = new Controllers {

    def upload: HttpRoutes[IO] = Controllers.upload(env)

    def health: HttpRoutes[IO] = Controllers.health(env)

    def search:HttpRoutes[IO] = Controllers.search(env)

    def static :HttpRoutes[IO] = Controllers.static(env)

    override def all: HttpRoutes[IO] = upload <+> health <+> search <+> static

  }

  def services: Services = new Services {
    def elasticSearch: ElasticSearchService = ElasticSearchService.instance(env)
  }

}
