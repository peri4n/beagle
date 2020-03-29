package io.beagle.controller

import io.beagle.search.Search
import io.beagle.search.SearchService
import cats.effect.IO
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.requests.searches.SearchResponse
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl._

object SearchController {

  def instance = Search.service map { SearchController(_).route }

  case class SearchSequenceRequest(sequence: String)

  case class SearchSequenceResponse(sequences: List[SearchHit])

  case class SearchHit(header: String, sequence: String)

}

case class SearchController(searchService: SearchService) extends Http4sDsl[IO] {

  import SearchController._

  implicit val entityDecoder = jsonOf[IO, SearchSequenceRequest]
  implicit val entityEncoder = jsonEncoderOf[IO, SearchSequenceResponse]

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
