package io.beagle.web.server.controller

import cats.effect.IO
import io.beagle.search.docs.SequenceDoc
import io.beagle.search.testsupport.SearchSuite
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

class SearchSequenceControllerTest extends SearchSuite {

  import SearchController._

  implicit val requestEncoder: EntityEncoder[IO, SearchSequenceRequest] = jsonEncoderOf[IO, SearchSequenceRequest]
  implicit val responseDecoder: EntityDecoder[IO, SearchSequenceResponse] = jsonOf[IO, SearchSequenceResponse]

  val service = setup().searchService

  val controller = SearchController(service).route.orNotFound

  test("finds a previously indexed sequences with shared n-grams") {
    val test = for {
      _ <- service.createSequenceIndex()
      _ <- service.index(SequenceDoc("header1", 1, "AAACGT"), refresh = true)
      _ <- service.index(SequenceDoc("header2", 1, "CAAAAT"), refresh = true)
      response <- controller.run(Request(
        method = Method.POST,
        uri = uri"/search",
        body = requestEncoder.toEntity(SearchSequenceRequest(sequence = "AAA")).body)
      )
    } yield response

    val responseCode = test.map { _.status }

    assertIO(responseCode, returns = Status.Ok)
  }
}
