import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.RootJsonFormat

import scala.io.StdIn

object App {

  private val Logger = LoggerFactory.getLogger(App.getClass.getName)

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    implicit val fastaEntryJsonFormat: RootJsonFormat[FastaEntry] = jsonFormat2(FastaEntry.apply)

    val staticResources =
      (get & pathPrefix("")) {
        (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
          getFromFile("dist/index.html")
        } ~ {
          getFromDirectory("dist")
        }
      }

    val uploadController = path("upload") {
      post {
        entity(as[String]) { fileUpload =>

          val entries = FastaParser.parse(fileUpload)
          println(entries)
          complete(entries)
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(staticResources ~ uploadController, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
