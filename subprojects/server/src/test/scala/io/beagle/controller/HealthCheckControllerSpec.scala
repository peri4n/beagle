package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Controller
import io.beagle.environments.TestEnv
import io.circe.generic.simple.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class HealthCheckControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  import HealthCheckController._

  implicit val requestEncoder: EntityEncoder[IO, HealthCheckRequest] = jsonEncoderOf[IO, HealthCheckRequest]
  implicit val responseDecoder: EntityDecoder[IO, HealthCheckResponse] = jsonOf[IO, HealthCheckResponse]

  val env = TestEnv.of[HealthCheckController]

  val controller = Controller.health(env).orNotFound

  "The HealthCheckController" should {

    "return true if ElasticSearch can be reached" in {
      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/health")))

      response must haveStatus(Status.Ok)
      response must haveBody(HealthCheckResponse(true))
    }

    "return false if ElasticSearch can't be reached" in {
      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/health")))

      response must haveStatus(Status.Ok)
      response must haveBody(HealthCheckResponse(false))
      }.pendingUntilFixed("This fails until port setting in test environment is easy again.")
  }
}
