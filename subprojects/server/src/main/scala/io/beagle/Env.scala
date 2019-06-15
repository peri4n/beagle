package io.beagle

import akka.http.scaladsl.server.Route
import cats.data.Reader
import com.typesafe.config.ConfigFactory
import io.beagle.components.{AkkaComponent, ControllerComponent, Controllers, SettingsComponent}
import io.beagle.directive.{FileUploadController, SearchSequenceController, Static}

trait Env extends SettingsComponent with ControllerComponent with AkkaComponent

object Env {

  val env = Reader[Env, Env](identity)

  val settings = env map (_.settings)

  val controllers = env map (_.controllers)

  val system = env map (_.system)

  val materializer = env map (_.materializer)

  object production extends Env {

    env =>

    override val settings: Settings = new Settings {

      private val config = ConfigFactory.load()

      def uiRoot = config.getString("ui.root")

      val elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

        def protocol = config.getString("elasticsearch.protocol")

        def host = config.getString("elasticsearch.host")

        def port = config.getInt("elasticsearch.port")

      }
    }

    val controllers = new Controllers {
      def fileUpload: Route = FileUploadController.route.run(env)

      def search: Route = SearchSequenceController.route.run(env)

      def static: Route = Static.route.run(env)
    }

  }

}
