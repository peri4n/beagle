package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Repositories
import io.beagle.domain.{SeqSet, SeqSetId}
import io.beagle.repository.seqset.SeqSetRepo
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl

object SequenceSetController {

  val PathName = "seqsets"

  def instance = Repositories.sequenceSet map { SequenceSetController(_).route }

  case class CreateSequenceSetRequest(name: String, alphabet: String)

  case class CreateSequenceSetResponse(name: String, alphabet: String)

}

case class SequenceSetController(repository: SeqSetRepo) extends Http4sDsl[IO] {

  import SequenceSetController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateSequenceSetRequest] { r =>
      repository.create(SeqSet(r.name)).flatMap(item => Ok(item.asJson))
    }

    case GET -> Root / PathName / LongVar(id) =>
      repository.find(SeqSetId(id)).flatMap(item => Ok(item.asJson))

    case DELETE -> Root / PathName / LongVar(id) =>
      repository.delete(SeqSetId(id)).flatMap(_ => Ok("success"))
  }

}
