package io.beagle.controller

import cats.effect.IO
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.cluster.ClusterHealthResponse
import io.beagle.components.Services
import io.beagle.service.ElasticSearchService
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.Implicits.global

object HealthCheckController {

  def instance = Services.elasticSearch map { HealthCheckController(_).route }

  case class HealthCheckRequest()

  case class HealthCheckResponse(healthy: Boolean)

  implicit val requestDecoder = jsonOf[IO, HealthCheckRequest]
  implicit val responseEncoder = jsonEncoderOf[IO, HealthCheckResponse]
}

case class HealthCheckController(elasticSearchService: ElasticSearchService) extends Http4sDsl[IO] {

  import HealthCheckController._

  implicit val timer = IO.timer(global)
  val route =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
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
