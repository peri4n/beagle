package io.beagle.controller

import cats.effect.{ContextShift, IO, Timer}
import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.cats.effect.instances._
import io.beagle.components._
import io.beagle.fasta.FastaParser
import io.circe.generic.auto._
import fs2._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FileUploadController {

  val instance = Settings.elasticSearch map { FileUploadController(_).route }

  case class FileUploadResponse(status: String)

}

case class FileUploadController(searchSettings: ElasticSearchSettings) extends Http4sDsl[IO] with ElasticDsl {

  import FileUploadController._

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  implicit val entityEncoder = jsonEncoderOf[IO, FileUploadResponse]

  val route =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "upload" =>
        Ok(req.body.through(FastaParser.parse)
          .groupWithin(20, 1.seconds)
          .map(chunk => chunk.toList.map { elem =>
            indexInto("fasta") fields(
              "header" -> elem.header,
              "sequence" -> elem.sequence
            )
          })
          .flatMap(r => Stream.eval(searchSettings.client.execute(bulk(r))))
          .map(_.toString))
    }
}
