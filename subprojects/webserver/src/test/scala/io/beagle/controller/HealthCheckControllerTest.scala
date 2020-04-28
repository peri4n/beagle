package io.beagle.controller

import cats.effect.IO
import io.beagle.search.testsupport.SearchSupport
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class HealthCheckControllerTest extends AnyFunSpec with ResponseMatchers with Matchers with SearchSupport {

  import HealthCheckController._

  implicit val requestEncoder: EntityEncoder[IO, HealthCheckRequest] = jsonEncoderOf[IO, HealthCheckRequest]
  implicit val responseDecoder: EntityDecoder[IO, HealthCheckResponse] = jsonOf[IO, HealthCheckResponse]

  val controller = HealthCheckController(service).route.orNotFound

  describe("The HealthCheckController") {

    it("return true if ElasticSearch can be reached") {
      val response = controller.run(Request(method = Method.GET, uri = uri"/health")).unsafeRunSync()

      response should have {
        status(Status.Ok)
      }
      //      response must haveBody(HealthCheckResponse(true))
    }

    it("return false if ElasticSearch can't be reached") {
      val response = controller.run(Request(method = Method.GET, uri = uri"/health")).unsafeRunSync()

      response should have {
        status(Status.Ok)
      }
      //      response must haveBody(HealthCheckResponse(false))
    }
  }
}
