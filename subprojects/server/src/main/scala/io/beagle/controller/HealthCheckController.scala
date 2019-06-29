package io.beagle.controller

import cats.effect.IO
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.http.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.http.{ElasticDsl, Response}
import io.beagle.components.{ElasticSearchSettings, Settings}
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}

object HealthCheckController {

  val instance = Settings.elasticSearch map { HealthCheckController(_).route }

  case class HealthCheckRequest()

  case class HealthCheckResponse(healthy: Boolean)

  implicit val requestDecoder: EntityDecoder[IO, HealthCheckRequest] = jsonOf[IO, HealthCheckRequest]
  implicit val responseEncoder: EntityEncoder[IO, HealthCheckResponse] = jsonEncoderOf[IO, HealthCheckResponse]

}

case class HealthCheckController(searchSettings: ElasticSearchSettings) extends Http4sDsl[IO] {

  import HealthCheckController._
  import ElasticDsl._

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

  val route =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        Ok(searchSettings.client.execute(clusterHealth())
          .redeem(_ => HealthCheckResponse(false), convertToResponse))
    }
}

