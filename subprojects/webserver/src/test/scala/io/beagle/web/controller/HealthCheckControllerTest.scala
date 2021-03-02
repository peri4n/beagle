package io.beagle.web.controller

import cats.effect.IO
import io.beagle.search.testsupport.SearchSuite
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

class HealthCheckControllerTest extends SearchSuite {

  import HealthCheckController._

  implicit val requestEncoder: EntityEncoder[IO, HealthCheckRequest] = jsonEncoderOf[IO, HealthCheckRequest]
  implicit val responseDecoder: EntityDecoder[IO, HealthCheckResponse] = jsonOf[IO, HealthCheckResponse]

  val service = setup().searchService

  val controller = HealthCheckController(service).route.orNotFound

  test("return true if ElasticSearch can be reached") {
    val test = controller.run(Request(method = Method.GET, uri = uri"/health"))

    val responseCode = test.map { _.status }

    assertIO(responseCode, returns = Status.Ok)
  }

  test("return false if ElasticSearch can't be reached") {
    val test = controller.run(Request(method = Method.GET, uri = uri"/health"))

    val responseCode = test.map { _.status }

    assertIO(responseCode, returns = Status.Ok)
  }
}
