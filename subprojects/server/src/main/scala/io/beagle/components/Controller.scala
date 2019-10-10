package io.beagle.components

import cats.effect.IO
import cats.syntax.semigroupk._
import io.beagle.Env
import io.beagle.controller._
import org.http4s.HttpRoutes

sealed trait Controller {

  def upload: HttpRoutes[IO]

  def health: HttpRoutes[IO]

  def search: HttpRoutes[IO]

  def dataset: HttpRoutes[IO]

  def login: HttpRoutes[IO]

  def static: HttpRoutes[IO]

  def all: HttpRoutes[IO] = static <+> endpoints

  def endpoints: HttpRoutes[IO] = upload <+> health <+> search <+> dataset

}

object Controller {

  def dataset = DatasetController.instance

  def health = HealthCheckController.instance

  def search = SearchSequenceController.instance

  def upload = FileUploadController.instance

  def login = LoginController.instance

  def static = StaticContentController.instance

  case class DefaultController(env: Env) extends Controller {

    def dataset = Controller.dataset(env)

    def upload = Controller.upload(env)

    def health = Controller.health(env)

    def search = Controller.search(env)

    def login = ??? //Controllers.login(env)

    def static = Controller.static(env)
  }
}
