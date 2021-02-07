package io.beagle.web.controller

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.DB
import io.beagle.persistence.service.DatasetService
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object DatasetController {

  val PathName = "seqsets"

  case class CreateSequenceSetRequest(name: String, userId: UserId, projectId: ProjectId)

  case class CreateSequenceSetResponse(name: String)

}

case class DatasetController(service: DatasetService, xa: Transactor[IO]) extends Http4sDsl[IO] {

  import DatasetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      service.create(Dataset(r.name, r.userId, r.projectId))
        .transact(xa)
        .flatMap(datasetItem => Ok(datasetItem.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      service.findById(DatasetId(id))
        .transact(xa)
        .flatMap(maybeDatasetItem => Ok(maybeDatasetItem.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      service.delete(DatasetId(id))
        .transact(xa)
        .flatMap(_ => Ok("success"))
  }

}
