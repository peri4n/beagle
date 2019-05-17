package io.beagle

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import io.beagle.components.Controllers
import io.beagle.directive.{FileUploadController, SearchSequenceController, Static}
import org.slf4j.LoggerFactory

import scala.io.StdIn

object App {

  private val Logger = LoggerFactory.getLogger(App.getClass.getName)

  private val environment = production

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")


    implicit val system = Env.system.run(environment)

    implicit val materializer = Env.materializer.run(environment)

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val bindingFuture = Http().bindAndHandle(environment.controllers.all, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

object production extends Env {

  env =>

  override def settings: Settings = new Settings {

    private val config = ConfigFactory.load()

    def uiRoot = config.getString("ui.root")

    def elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      def protocol = config.getString("elasticsearch.protocol")

      def host = config.getString("elasticsearch.host")

      def port = config.getInt("elasticsearch.port")

    }
  }

  def controllers = new Controllers {
    def fileUpload: Route = FileUploadController.route.run(env)

    def search: Route = SearchSequenceController.route.run(env)

    def static: Route = Static.route.run(env)
  }

}
