package io.beagle.controller

import cats.effect.IO
import com.sksamuel.elastic4s.Response
import com.sksamuel.elastic4s.requests.cluster.ClusterHealthResponse
import io.beagle.components.Search
import io.beagle.service.SearchService
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object HealthCheckController {

  val PathName = "health"

  def instance = Search.service map { HealthCheckController(_).route }

  case class HealthCheckRequest()

  case class HealthCheckResponse(healthy: Boolean)

  implicit val requestDecoder = jsonOf[IO, HealthCheckRequest]
  implicit val responseEncoder = jsonEncoderOf[IO, HealthCheckResponse]
}

case class HealthCheckController(elasticSearchService: SearchService) extends Http4sDsl[IO] {

  import HealthCheckController._

  val route =
    HttpRoutes.of[IO] {
      case GET -> Root / PathName =>
        Ok(elasticSearchService.connectionCheck()
          .redeem(_ => HealthCheckResponse(false), convertToResponse))
    }

  def convertToResponse(value: Response[ClusterHealthResponse]): HealthCheckResponse = {
    if (value.isError) {
      HealthCheckResponse(false)
    } else {
      if (value.result.status == "healthy") {
        HealthCheckResponse(false)
      } else {
        HealthCheckResponse(true)
      }
    }
  }
}
