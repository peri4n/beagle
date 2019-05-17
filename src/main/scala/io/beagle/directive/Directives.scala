package io.beagle.directive

import akka.http.scaladsl.server.Directives._

object Directives {

  def all = for {
    upload <- FileUploadController.route
    search <- SearchSequenceController.route
    static <- Static.route
  } yield upload ~ search ~ static

}
