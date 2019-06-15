package io.beagle

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.embedded.LocalNode
import io.beagle.components.Controllers
import io.beagle.directive.{FileUploadController, SearchSequenceController, Static}

case class TestEnv(override val system: ActorSystem) extends Env {

  env =>

  def settings = new Settings {

    def uiRoot: String = "dist"

    def elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      private val localNode = LocalNode("test-cluster", "/tmp/datapath")

      override val client = localNode.client(true)

      def protocol: String = ???

      def host: String = ???

      def port: Int = ???
    }
  }

  def controllers = new Controllers {
    def fileUpload: Route = FileUploadController.route.run(env)

    def search: Route = SearchSequenceController.route.run(env)

    def static: Route = Static.route.run(env)
  }
}
