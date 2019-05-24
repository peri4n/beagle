package io.beagle.directive

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import com.sksamuel.elastic4s.http.ElasticDsl
import com.typesafe.scalalogging.Logger
import io.beagle.fasta.FastaParser
import io.beagle.{ElasticSearchSettings, Env}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import javax.ws.rs.{POST, Path}
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse

object FileUploadActor {

  case class FileUploadRequest(text: String)

  case class FileUploadResponse(status: String)

}

class FileUploadActor(settings: ElasticSearchSettings) extends Actor with ElasticDsl {

  import FileUploadActor._

  private val Log = Logger(classOf[FileUploadActor])

  def receive: Receive = {
    case FileUploadRequest(text) =>
      logger.info("received request")
      val requests = FastaParser.parse(text) map { elem =>
        indexInto("fasta", "sequence") fields(
          "header" -> elem.header,
          "sequence" -> elem.sequence
        )
      }

      val future = settings.client.execute {
        bulk(requests)
      }

      sender ! FileUploadResponse("success")
  }

}

object FileUploadController {

  val route = Env.env map { env => new FileUploadController(env.system.actorOf(Props(new FileUploadActor(env.settings.elasticSearch))))(env.system.dispatcher).upload }

}

@Path("")
class FileUploadController(fileUploadActor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with DefaultJsonProtocol with SprayJsonSupport {

  import FileUploadActor._

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat1(FileUploadRequest)
  implicit val responseFormat = jsonFormat1(FileUploadResponse)

  @POST
  @Operation(summary = "FASTA upload", description = "Upload FASTA files",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[FileUploadRequest])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "File upload response",
        content = Array(new Content(schema = new Schema(implementation = classOf[FileUploadResponse])))),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def upload = path("upload") {
    post {
      entity(as[FileUploadRequest]) { request =>
        onComplete(( fileUploadActor ? request ).mapTo[FileUploadResponse]) {
          case Success(value) => complete("success")
          case Failure(throwable) => failWith(throwable)
        }
      }
    }
  }
}
