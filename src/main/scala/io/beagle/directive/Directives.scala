package io.beagle.directive

import akka.http.scaladsl.server.Directives._

object Directives {

  def all = for {
    upload <- FileUpload.uploadController
    search <- Search.searchController
    static <- Static.routes
  } yield upload ~ search ~ static

}
