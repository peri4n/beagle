package io.beagle.directive

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.sksamuel.elastic4s.http.ElasticDsl
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.beagle.{ElasticSearchSettings, Env}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object SearchSequenceActor {

  case class SearchSequenceRequest(sequence: String)

  case class SearchSequenceResponse(sequences: List[SearchHit])

  case class SearchHit(header: String, sequence: String)

}

class SearchSequenceActor(settings: ElasticSearchSettings) extends Actor with ElasticDsl with ActorLogging {

  import SearchSequenceActor._
  import context.dispatcher

  def receive: Receive = {
    case SearchSequenceRequest(sequence) =>
      log.info("test")

      val response = settings.client.execute {
        search("fasta") query sequence
      }.await(2.seconds)


      sender ! SearchSequenceResponse(
        response.result.hits.hits.map { hit =>
          SearchHit(
            hit.sourceAsMap.getOrElse("header", "").toString,
            hit.sourceAsMap.getOrElse("sequence", "").toString)
        }.toList)
  }
}

object SearchSequenceController {

  val route = Env.env map { env => new SearchSequenceController(env.system.actorOf(Props(new SearchSequenceActor(env.settings.elasticSearch))))(env.system.dispatcher).search }

}

class SearchSequenceController(searchActor: ActorRef)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import SearchSequenceActor._
  import akka.http.scaladsl.server.Directives._
  import io.circe.generic.auto._

  implicit val timeout = Timeout(2.seconds)

  val search =
    path("search") {
      post {
        entity(as[SearchSequenceRequest]) { request =>
          onComplete(( searchActor ? request ).mapTo[SearchSequenceResponse]) {
            case Success(value) => complete(value)
            case Failure(throwable) => failWith(throwable)
          }
        }
      }
    }
}
