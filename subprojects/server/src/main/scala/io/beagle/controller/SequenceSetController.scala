package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Repositories
import io.beagle.domain.SeqSet
import io.beagle.repository.seqset.SeqSetRepo
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

object SequenceSetController {

  val instance = Repositories.sequenceSet map { SequenceSetController(_).route }

  case class CreateSequenceSetRequest(name: String, alphabet: String)

  case class CreateSequenceSetResponse(name: String, alphabet: String)

}

case class SequenceSetController(repository: SeqSetRepo) extends Http4sDsl[IO] {

  import SequenceSetController._

  implicit val entityDecoder: EntityDecoder[IO, CreateSequenceSetRequest] = jsonOf[IO, CreateSequenceSetRequest]

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / "sequences" =>
      for {
        request <- req.as[CreateSequenceSetRequest]
        sequenceSetView <- repository.create(SeqSet(request.name))
        response <- Ok(sequenceSetView)
      } yield response
  }

}
