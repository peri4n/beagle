package io.beagle.controller

import cats.effect.IO
import io.beagle.Env.TestEnv
import io.beagle.components.Web
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.specs2.mutable.Specification

class HealthCheckControllerSpec extends Specification with ResponseMatchers {

  import HealthCheckController._

  implicit val requestEncoder: EntityEncoder[IO, HealthCheckRequest] = jsonEncoderOf[IO, HealthCheckRequest]
  implicit val responseDecoder: EntityDecoder[IO, HealthCheckResponse] = jsonOf[IO, HealthCheckResponse]

  val controller = runAwait(for {
    env <- TestEnv.of[HealthCheckController]
    controller = Web.health(env).orNotFound
  } yield controller)

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
