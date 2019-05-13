package io.beagle

import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import io.beagle.directive.Directives
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

    val bindingFuture = Http().bindAndHandle(Env.route.run(environment), "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

object production extends Env {

  override def settings: Settings = new Settings {

    private val config = ConfigFactory.load()

    def uiRoot = config.getString("ui.root")

    def elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      def elasticSearchHost = config.getString("elasticsearch.host")

      def elasticSearchPort = config.getInt("elasticsearch.port")

    }
  }

  def route = Directives.all.run(this)
}
