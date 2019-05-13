package io.beagle.components
import akka.http.scaladsl.server.Route

trait ControllerComponent {

  def route: Route

}
