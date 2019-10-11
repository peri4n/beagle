package io.beagle.controller

import cats.effect.IO
import com.sksamuel.elastic4s.ElasticDsl
import io.beagle.components._
import io.beagle.fasta.FastaParser
import io.beagle.service.ElasticSearchService
import io.circe.generic.simple.auto._
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.circe._
import io.beagle.Env
import org.http4s.dsl.Http4sDsl

import scala.concurrent.duration._

object FileUploadController {

  def instance = for {
    ex <- Env.execution
    elasticSearch <- Service.elasticSearch
  } yield FileUploadController(ex, elasticSearch).route

  case class FileUploadResponse(status: String)

}

case class FileUploadController(execution: Execution, elasticService: ElasticSearchService) extends Http4sDsl[IO] with ElasticDsl {

  import FileUploadController._

  import execution._

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
