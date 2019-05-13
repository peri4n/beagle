package io.beagle.directive

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.data.Reader
import io.beagle.Env

object Static {

  val routes: Reader[Env, Route] = Env.settings map { settings =>
    (get & pathPrefix("")) {
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromFile(settings.uiRoot + "/index.html")
      } ~ {
        getFromDirectory(settings.uiRoot)
      }
    }
  }
}
