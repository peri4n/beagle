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

  def endpoints: HttpRoutes[IO] = upload <+> health <+> search <+> dataset <+> login

}

object Controller {

  def dataset = DatasetController.instance

  def health = HealthCheckController.instance

  def search = SearchSequenceController.instance

  def upload = FileUploadController.instance

  def login = LoginController.instance

  def static = StaticContentController.instance

  case class DefaultController(env: Env) extends Controller {

    lazy val dataset = Controller.dataset(env)

    lazy val upload = Controller.upload(env)

    lazy val health = Controller.health(env)

    lazy val search = Controller.search(env)

    lazy val login = Controller.login(env)

    lazy val static = Controller.static(env)
  }
}