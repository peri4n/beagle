package io.beagle.components

import cats.effect.IO
import cats.implicits._
import io.beagle.controller.{FileUploadController, HealthCheckController, SearchSequenceController, StaticContentController}
import org.http4s.HttpRoutes

trait Controllers {

  def upload: HttpRoutes[IO]

  def health: HttpRoutes[IO]

  def search: HttpRoutes[IO]

  def static: HttpRoutes[IO]

  def all = upload <+> search <+> health
}

object Controllers {
  def health = HealthCheckController.instance

  def search = SearchSequenceController.instance

  def upload = FileUploadController.instance

  def static = StaticContentController.instance
}
