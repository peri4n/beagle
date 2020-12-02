package io.beagle.web.controller

import cats.effect.IO
import io.beagle.search.docs.FastaDoc
import io.beagle.search.testsupport.SearchSupport
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SearchSequenceControllerTest extends AnyFunSpec with Matchers with ResponseMatchers with SearchSupport {

  import SearchController._

  implicit val requestEncoder: EntityEncoder[IO, SearchSequenceRequest] = jsonEncoderOf[IO, SearchSequenceRequest]
  implicit val responseDecoder: EntityDecoder[IO, SearchSequenceResponse] = jsonOf[IO, SearchSequenceResponse]

  val controller = SearchController(service).route.orNotFound

  describe("The SearchSequenceController" ) {
    it("finds a previously indexed sequences with shared n-grams" ) {
      val test = for {
        _ <- service.createSequenceIndex()
        _ <- service.index(FastaDoc("header1", 1, "AAACGT"), refresh = true)
        _ <- service.index(FastaDoc("header2", 1, "CAAAAT"), refresh = true)
        response <- controller.run(Request(
          method = Method.POST,
          uri = uri"/search",
          body = requestEncoder.toEntity(SearchSequenceRequest(sequence = "AAA")).body)
        )
      } yield response

      val response = test.unsafeRunSync()

      response should have {
        status(Status.Ok)
      }
    }

  }
}
