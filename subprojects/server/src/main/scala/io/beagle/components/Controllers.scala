package io.beagle.components

import cats.effect.IO
import cats.syntax.semigroupk._
import io.beagle.controller._
import org.http4s.HttpRoutes

trait Controllers {

  def upload: HttpRoutes[IO]

  def health: HttpRoutes[IO]

  def search: HttpRoutes[IO]

  def seqset: HttpRoutes[IO]

  def static: HttpRoutes[IO]

  def all: HttpRoutes[IO] = static <+> endpoints

  def endpoints: HttpRoutes[IO] = upload <+> health <+> search <+> seqset

}

object Controllers {

  def dataset = DatasetController.instance

  def health = HealthCheckController.instance

  def search = SearchSequenceController.instance

  def upload = FileUploadController.instance

  def static = StaticContentController.instance

}
