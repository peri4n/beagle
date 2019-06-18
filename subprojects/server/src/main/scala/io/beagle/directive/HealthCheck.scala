package io.beagle.directive

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.sksamuel.elastic4s.http.RequestSuccess
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.beagle.{ElasticSearchSettings, Env}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object HealthCheckActor {

  case class HealthCheckRequest()

  case class HealthCheckResponse(healthy: Boolean)

}

class HealthCheckActor(settings: ElasticSearchSettings) extends Actor {

  import HealthCheckActor._
  import com.sksamuel.elastic4s.http.ElasticDsl._
  import context.dispatcher

  def receive: Receive = {
    case HealthCheckRequest =>
      settings.client.execute { clusterHealth() }
        .recover {
          // in case the connection does not succeed
          case _ => HealthCheckResponse(false)
        }
        .map {
          case r: RequestSuccess[_] => HealthCheckResponse(true)
          case _ => HealthCheckResponse(false)
        }.pipeTo(sender)
  }
}

object HealthCheckController {
  val route = Env.env map { env => new HealthCheckController(env.system.actorOf(Props(new HealthCheckActor(env.settings.elasticSearch))))(env.system.dispatcher).health }
}

class HealthCheckController(healthCheckActor: ActorRef)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import HealthCheckActor._
  import akka.http.scaladsl.server.Directives._
  import io.circe.generic.auto._

  implicit val timeout = Timeout(2.seconds)

  val health =
    path("health") {
      get {
        onComplete(( healthCheckActor ? HealthCheckRequest ).mapTo[HealthCheckResponse]) {
          case Success(value) => if (value.healthy) complete(value) else complete(StatusCodes.ServiceUnavailable)
          case Failure(throwable) => failWith(throwable)
        }
      }
    }
}
