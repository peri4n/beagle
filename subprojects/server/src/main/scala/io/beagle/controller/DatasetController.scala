package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Repositories
import io.beagle.domain.{Dataset, DatasetId}
import io.beagle.repository.dataset.DatasetRepo
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl

object DatasetController {

  val PathName = "seqsets"

  def instance = Repositories.sequenceSet map { DatasetController(_).route }

  case class CreateSequenceSetRequest(name: String, alphabet: String)

  case class CreateSequenceSetResponse(name: String, alphabet: String)

}

case class DatasetController(repository: DatasetRepo) extends Http4sDsl[IO] {

  import DatasetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      repository.create(Dataset(r.name)).flatMap(item => Ok(item.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      repository.find(DatasetId(id)).flatMap(item => Ok(item.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      repository.delete(DatasetId(id)).flatMap(_ => Ok("success"))
  }

}
