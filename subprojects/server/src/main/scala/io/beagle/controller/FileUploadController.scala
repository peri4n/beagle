package io.beagle.controller

import cats.effect.{ContextShift, IO, Timer}
import com.sksamuel.elastic4s.ElasticDsl
import io.beagle.components._
import io.beagle.fasta.FastaParser
import io.beagle.service.ElasticSearchService
import io.circe.generic.simple.auto._
import fs2._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FileUploadController {

  def instance = Service.elasticSearch map { FileUploadController(_).route }

  case class FileUploadResponse(status: String)

}

case class FileUploadController(elasticService: ElasticSearchService) extends Http4sDsl[IO] with ElasticDsl {

  import FileUploadController._

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  implicit val entityEncoder = jsonEncoderOf[IO, FileUploadResponse]

  val route =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "upload" =>
        req.body.through(FastaParser.parse)
          .groupWithin(20, 1.seconds)
          .map(chunk => chunk.toList)
          .flatMap(r => Stream.eval(elasticService.index(r)))
          .compile.toList.unsafeRunSync()

        Ok(FileUploadResponse("success"))
    }
}
