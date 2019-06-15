package io.beagle.directive

object HealthCheckActor {

}

class HealthCheckController {

  import akka.http.scaladsl.server.Directives._

  val check =
    path("health") {
      get {
        complete("ok")
      }
    }

}
