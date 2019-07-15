package io.beagle.controller

import cats.effect.IO
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import io.beagle.components._
import io.beagle.service.ElasticSearchService
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}

object SearchSequenceController {

  val instance = for {
    settings <- Settings.elasticSearch
    service <- Services.elasticSearch
  } yield SearchSequenceController(settings, service).route

  case class SearchSequenceRequest(sequence: String)

  case class SearchSequenceResponse(sequences: List[SearchHit])

  case class SearchHit(header: String, sequence: String)

}

case class SearchSequenceController(searchSettings: ElasticSearchSettings, searchService: ElasticSearchService) extends Http4sDsl[IO] {

  import SearchSequenceController._

  implicit val entityDecoder: EntityDecoder[IO, SearchSequenceRequest] = jsonOf[IO, SearchSequenceRequest]
  implicit val entityEncoder: EntityEncoder[IO, SearchSequenceResponse] = jsonEncoderOf[IO, SearchSequenceResponse]

  val route =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "search" =>
        for {
          request <- req.as[SearchSequenceRequest]
          esResponse <- searchService.find(request.sequence)
          resp <- Ok(convertResponse(esResponse))
        } yield resp
    }

  private def convertResponse(response: Response[SearchResponse]): SearchSequenceResponse = {
    SearchSequenceResponse(
      response.result.hits.hits.map { hit =>
        SearchHit(
          hit.sourceAsMap.getOrElse("header", "").toString,
          hit.sourceAsMap.getOrElse("sequence", "").toString)
      }.toList)
  }
}
