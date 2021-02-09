package io.beagle.web.controller

import cats.effect.IO
import com.sksamuel.elastic4s.ElasticDsl
import fs2.Stream
import io.beagle.exec.Exec
import io.beagle.parser.fasta.{FastaParser, FastaSeq}
import io.beagle.search.SearchService
import io.beagle.search.docs.SequenceDoc
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.duration._

object FileUploadController {

  case class FileUploadResponse(status: String)

}

case class FileUploadController(execution: Exec, elasticService: SearchService) extends Http4sDsl[IO] with ElasticDsl {

  import FileUploadController._

  implicit val timer = execution.timer
  implicit val e = execution.shift

  implicit val entityEncoder = jsonEncoderOf[IO, FileUploadResponse]

  def toFastaDoc(sequences: List[FastaSeq]): SequenceDoc = ???

  val route =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "upload" =>
        req.body.through(FastaParser.parse)
          .groupWithin(20, 1.seconds)
          .map(chunk => chunk.toList)
          .flatMap(r => Stream.eval(elasticService.index(toFastaDoc(r))))
          .compile.toList.unsafeRunSync()

        Ok(FileUploadResponse("success"))
    }
}
