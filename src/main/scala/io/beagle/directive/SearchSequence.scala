package io.beagle.directive

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import cats.instances.future
import com.sksamuel.elastic4s.http.search.SearchHits
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticDsl, ElasticProperties}
import com.typesafe.scalalogging.Logger
import io.beagle.{ElasticSearchSettings, Env}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object SearchSequenceActor {

  case class SearchSequenceRequest(sequence: String)

  case class SearchSequenceResponse(sequences: List[SearchHit])

  case class SearchHit(header: String, sequence: String)

}

class SearchSequenceActor(settings: ElasticSearchSettings) extends Actor with ElasticDsl {

  import SearchSequenceActor._
  import context.dispatcher

  val Log = Logger(classOf[SearchSequenceActor])

  val client = ElasticClient(ElasticProperties(s"${ settings.protocol }://${ settings.host }:${ settings.port }"))

  def receive: Receive = {
    case SearchSequenceRequest(sequence) =>
      val future = client.execute {
        search("fasta") query sequence
      }

      val response = Await.result(future, 2.seconds)

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

class SearchSequenceController(searchActor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with DefaultJsonProtocol with SprayJsonSupport {

  import SearchSequenceActor._

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat1(SearchSequenceRequest)
  implicit val hitFormat = jsonFormat2(SearchHit)
  implicit val responseFormat = jsonFormat1(SearchSequenceResponse)

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
