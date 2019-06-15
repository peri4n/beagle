package io.beagle

import akka.http.scaladsl.Http
import akka.pattern.retry
import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, NGramTokenizer, StandardAnalyzer, UppercaseTokenFilter}
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.mappings.{MappingDefinition, TextField}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object App {

  private val Logger = LoggerFactory.getLogger(App.getClass.getName)

  private val environment = Env.production

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    implicit val system = Env.system.run(environment)

    implicit val scheduler = system.scheduler

    implicit val materializer = Env.materializer.run(environment)

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    import com.sksamuel.elastic4s.http.ElasticDsl._

    def connectionCheck = environment.settings.elasticSearch.client.execute { clusterHealth() }

    Await.result(retry(() => connectionCheck, 100, 1.seconds), Duration.Inf)

    val createFastaIndex: Future[Response[CreateIndexResponse]] = environment.settings.elasticSearch.client.execute {
      createIndex("fasta").mappings(
        MappingDefinition("sequence").as(
          TextField("header").analyzer(StandardAnalyzer),
          TextField("sequence").analyzer("custom"))
      ).analysis(
        CustomAnalyzerDefinition(
          "custom",
          NGramTokenizer("nGram", 3, 5),
          UppercaseTokenFilter
        )
      )
    }
    Await.result(createFastaIndex.fallbackTo(createFastaIndex), 2.seconds)

    val bindingFuture = Http().bindAndHandle(environment.controllers.all, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}

