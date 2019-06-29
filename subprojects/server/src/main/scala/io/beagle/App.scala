package io.beagle

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.all._
import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, NGramTokenizer, StandardAnalyzer, UppercaseTokenFilter}
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.mappings.{MappingDefinition, TextField}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import io.beagle.components.{Env, Services}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  private val environment = Env.selection

  def connectionCheck(env: Env): IO[Response[ClusterHealthResponse]] = env.settings.elasticSearch.client.execute { clusterHealth() }

  def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)(implicit timer: Timer[IO]): IO[A] = {
    ioa.handleErrorWith { error =>
      if (maxRetries > 0)
        IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
      else
        IO.raiseError(error)
    }
  }

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")
    Logger.info(environment.toString)

    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)

    val preconditions = for {
      _ <- retryWithBackoff(connectionCheck(environment), 5.seconds, 100)
      _ <- Services.elasticSearch(environment).createSequenceIndex()
    } yield ()

    preconditions.unsafeRunSync()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO].bindHttp(8080).withHttpApp(environment.controllers.all.orNotFound).resource
    server.use(_ => IO.never).start.unsafeRunSync()
  }

}

