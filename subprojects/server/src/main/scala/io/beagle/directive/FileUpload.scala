package io.beagle.directive

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.sksamuel.elastic4s.http.ElasticDsl
import com.typesafe.scalalogging.Logger
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.beagle.fasta.FastaParser
import io.beagle.{ElasticSearchSettings, Env}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object FileUploadActor {

  case class FileUploadRequest(text: String)

  case class FileUploadResponse(status: String)

  def props(elasticSearchSettings: ElasticSearchSettings) = Props(new FileUploadActor(elasticSearchSettings))

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

      settings.client.execute {
        bulk(requests)
      }.await(2.seconds)

      sender ! FileUploadResponse("success")
  }

}

object FileUploadController {

  val route = Env.env map { env => new FileUploadController(env.system.actorOf(FileUploadActor.props(env.settings.elasticSearch)))(env.system.dispatcher).upload }

}

class FileUploadController(fileUploadActor: ActorRef)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import FileUploadActor._

  implicit val timeout = Timeout(2.seconds)

  import io.circe.generic.auto._

  def upload = path("upload") {
    formField('file.*) { request =>
      onComplete(( fileUploadActor ? FileUploadRequest(request.mkString("\n")) ).mapTo[FileUploadResponse]) {
        case Success(value) => complete(value)
        case Failure(throwable) => failWith(throwable)
      }
    }
  }
}
