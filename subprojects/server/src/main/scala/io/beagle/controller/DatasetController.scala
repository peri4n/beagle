package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.Env
import io.beagle.components.Repository
import io.beagle.components.persistence.Persistence
import io.beagle.domain.{Dataset, DatasetId, ProjectId}
import io.beagle.repository.dataset.DatasetRepo
import io.circe.generic.simple.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object DatasetController {

  val PathName = "seqsets"

  def instance = for {
    xa <- Env.transaction
    repo <- Repository.dataset
  } yield DatasetController(repo, xa).route

  case class CreateSequenceSetRequest(name: String, projectId: ProjectId)

  case class CreateSequenceSetResponse(name: String)

}

case class DatasetController(repository: DatasetRepo, xa: Persistence) extends Http4sDsl[IO] {

  import DatasetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      repository.create(Dataset(r.name, r.projectId ))
        .transact(xa.transactor)
        .flatMap(datasetItem => Ok(datasetItem.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      repository.findById(DatasetId(id))
        .transact(xa.transactor)
        .flatMap(maybeDatasetItem => Ok(maybeDatasetItem.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      repository.delete(DatasetId(id))
        .transact(xa.transactor)
        .flatMap(_ => Ok("success"))
  }

}
