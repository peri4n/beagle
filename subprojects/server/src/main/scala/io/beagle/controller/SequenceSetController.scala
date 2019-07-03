package io.beagle.controller

import cats.effect.IO
import io.beagle.components.{DatabaseSettings, Repositories, Settings}
import io.beagle.domain.DNA
import io.beagle.repository.SequenceSetRepo
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

object SequenceSetController {

  val instance = for {
    settings <- Settings.database
    repo <- Repositories.sequenceSet
  } yield SequenceSetController(settings, repo).route

  case class CreateSequenceSetRequest(name: String, alphabet: String)

}

case class SequenceSetController(settings: DatabaseSettings, repository: SequenceSetRepo) extends Http4sDsl[IO] {

  import SequenceSetController._

  implicit val entityDecoder: EntityDecoder[IO, CreateSequenceSetRequest] = jsonOf[IO, CreateSequenceSetRequest]

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / "sequences" =>
      for {
        request <- req.as[CreateSequenceSetRequest]
        sequenceSet <- repository.create(request.name, DNA)
        response <- Ok(sequenceSet)
      } yield response
  }

}
