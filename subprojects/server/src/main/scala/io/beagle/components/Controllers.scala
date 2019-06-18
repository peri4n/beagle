package io.beagle.components

import akka.http.scaladsl.server.{Directives, Route}
import io.beagle.Env

trait Controllers extends Directives {

  def static: Route

  def fileUpload: Route

  def search: Route

  def health: Route

  def all = static ~ fileUpload ~ search ~ health

}

object Controllers {

  val fileUpload = Env.controllers map { _.fileUpload }

  val search = Env.controllers map { _.search }

  val all = Env.controllers map { controllers =>
    new Controllers {
      def fileUpload: Route = controllers.fileUpload

      def search: Route = controllers.search

      def static: Route = controllers.static

      def health: Route = controllers.health
    }
  }

}
