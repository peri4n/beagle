package io.beagle.controller

import cats.effect.IO
import io.beagle.components.{Controllers, Services}
import io.beagle.environments.Test
import io.beagle.fasta.FastaEntry
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class SearchSequenceControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  import SearchSequenceController._

  implicit val requestEncoder: EntityEncoder[IO, SearchSequenceRequest] = jsonEncoderOf[IO, SearchSequenceRequest]
  implicit val responseDecoder: EntityDecoder[IO, SearchSequenceResponse] = jsonOf[IO, SearchSequenceResponse]

  "The SearchSequenceController" should {
    "finds a previously indexed sequences with shared ngrams" in {
      val environment = Test.of[SearchSequenceControllerSpec]
      val es = Services.elasticSearch(environment)
      val testCase = for {
        _ <- es.createSequenceIndex()
        _ <- es.index(FastaEntry("header1", "AAACGT"), refresh = true)
        _ <- es.index(FastaEntry("header2", "CAAAAT"), refresh = true)
        response <- Controllers.search(environment).orNotFound.run(
          Request(method = Method.POST, uri = uri"/search", body = requestEncoder.toEntity(SearchSequenceRequest(sequence = "AAA")).body)
        )
      } yield response

      val response = runAwait(testCase)

      response must haveStatus(Status.Ok)
      response must haveBody { body: SearchSequenceResponse =>
        body.sequences must containAllOf(List(SearchHit("header1", "AAACGT"), SearchHit("header2", "CAAAAT")))
      }
    }

  }
}
