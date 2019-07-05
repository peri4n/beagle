package io.beagle.controller

import cats.effect.IO
import io.beagle.components.ElasticSearchSettings.LocalElasticSearchSettings
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class HealthCheckControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  import HealthCheckController._

  implicit val requestEncoder: EntityEncoder[IO, HealthCheckRequest] = jsonEncoderOf[IO, HealthCheckRequest]
  implicit val responseDecoder: EntityDecoder[IO, HealthCheckResponse] = jsonOf[IO, HealthCheckResponse]

  "The HealthCheckController" should {
    "returns true if ElasticSearch can be reached" in {
      val settings = LocalElasticSearchSettings()
      val response = runAwait(new HealthCheckController(settings).route.orNotFound.run(
        Request(method = Method.GET, uri = uri"/health")
      ))

      response must haveStatus(Status.Ok)
      response must haveBody(HealthCheckResponse(true))
    }
    "returns false if ElasticSearch can't be reached" in {
      val settings = LocalElasticSearchSettings().copy(port = 1234)
      val response = runAwait(new HealthCheckController(settings).route.orNotFound.run(
        Request(method = Method.GET, uri = uri"/health")
      ))

      response must haveStatus(Status.Ok)
      response must haveBody(HealthCheckResponse(false))
    }
  }
}
