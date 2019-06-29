package io.beagle.controller

import cats.effect.IO
import io.beagle.components._
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.indexes.IndexRequest
import io.beagle.components.ElasticSearchSettings
import io.beagle.fasta.FastaParser
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.{HttpRoutes, UrlForm}

object FileUploadController {

  val instance = Settings.elasticSearch map { FileUploadController(_).route }

  case class FileUploadResponse(status: String)

}

case class FileUploadController(searchSettings: ElasticSearchSettings) extends Http4sDsl[IO] with ElasticDsl {

  import FileUploadController._

  implicit val entityEncoder = jsonEncoderOf[IO, FileUploadResponse]

  val route =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "upload" =>
        req.decode[UrlForm] { form =>
          val indexRequests = listOfIndexRequests(form.values("file").toList.mkString("\n"))
          searchSettings.client.execute(bulk(indexRequests)).unsafeRunSync()
          Ok(FileUploadResponse("success"))
        }
    }

  def listOfIndexRequests(content: String): List[IndexRequest] = {
    FastaParser.parse(content) map { elem =>
      indexInto("fasta", "sequence") fields(
        "header" -> elem.header,
        "sequence" -> elem.sequence
      )
    }
  }
}
