package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.components.{DatabaseSettings, Repositories}
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
    repo <- Repositories.dataset
    xa <- DatabaseSettings.transactor
  } yield DatasetController(repo, xa).route

  case class CreateSequenceSetRequest(name: String, projectId: ProjectId)

  case class CreateSequenceSetResponse(name: String)

}

case class DatasetController(repository: DatasetRepo, xa: Transactor[IO]) extends Http4sDsl[IO] {

  import DatasetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      repository.create(Dataset(r.name, r.projectId ))
        .transact(xa)
        .flatMap(datasetItem => Ok(datasetItem.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      repository.findById(DatasetId(id))
        .transact(xa)
        .flatMap(maybeDatasetItem => Ok(maybeDatasetItem.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      repository.delete(DatasetId(id))
        .transact(xa)
        .flatMap(_ => Ok("success"))
  }

}
