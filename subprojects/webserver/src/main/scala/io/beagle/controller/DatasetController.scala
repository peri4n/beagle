package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.Persistence
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

case class DatasetController(xa: Persistence) extends Http4sDsl[IO] {

  import DatasetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      DatasetService.create(Dataset(r.name, r.userId, r.projectId))
        .transact(xa.transactor)
        .flatMap(datasetItem => Ok(datasetItem.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      DatasetService.findById(DatasetId(id))
        .transact(xa.transactor)
        .flatMap(maybeDatasetItem => Ok(maybeDatasetItem.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      DatasetService.delete(DatasetId(id))
        .transact(xa.transactor)
        .flatMap(_ => Ok("success"))
  }

}
