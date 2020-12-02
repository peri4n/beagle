package io.beagle.web.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.PersistenceEnv
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

case class DatasetController(persistence: PersistenceEnv) extends Http4sDsl[IO] {

  import DatasetController._

  val service = persistence.datasetService

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      service.create(Dataset(r.name, r.userId, r.projectId))
        .transact(persistence.transactor)
        .flatMap(datasetItem => Ok(datasetItem.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      service.findById(DatasetId(id))
        .transact(persistence.transactor)
        .flatMap(maybeDatasetItem => Ok(maybeDatasetItem.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      service.delete(DatasetId(id))
        .transact(persistence.transactor)
        .flatMap(_ => Ok("success"))
  }

}
