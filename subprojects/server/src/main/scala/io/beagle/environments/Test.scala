package io.beagle.environments

import cats.effect.IO
import io.beagle.components._
import io.beagle.service.ElasticSearchService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Allow

import scala.reflect.ClassTag

case class Test(name: String) extends Env {

  env =>

  val settings = new Settings {

    val uiRoot: String = "dist"

    val elasticSearch: ElasticSearchSettings = new ElasticSearchSettings {

      override val sequenceIndex = s"${ name.toLowerCase() }-${ System.currentTimeMillis() }-fasta"

      val protocol = "http"

      val host = "localhost"

      val port = 9200
    }
  }

  val controllers = new Controllers with Http4sDsl[IO] {

    def upload = Controllers.upload(env)

    def health = Controllers.health(env)

    def search = Controllers.search(env)

    def static = HttpRoutes.of[IO] { case _ -> Root => MethodNotAllowed(Allow(GET)) } // not needed while testing
  }

  val services: Services = new Services {
    def elasticSearch: ElasticSearchService = ElasticSearchService.instance(env)
  }
}

object Test {
  def of[A: ClassTag] = Test(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
