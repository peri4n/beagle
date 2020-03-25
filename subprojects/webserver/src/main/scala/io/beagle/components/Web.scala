package io.beagle.components

import cats.data.Reader
import cats.effect.IO
import cats.syntax.semigroupk._
import io.beagle.Env
import io.beagle.controller._
import org.http4s.HttpRoutes

sealed trait Web {

  def uiRoot: String

  def upload: HttpRoutes[IO]

  def health: HttpRoutes[IO]

  def search: HttpRoutes[IO]

  def dataset: HttpRoutes[IO]

  def login: HttpRoutes[IO]

  def static: HttpRoutes[IO]

  def all: HttpRoutes[IO] = static <+> endpoints

  def endpoints: HttpRoutes[IO] = upload <+> health <+> search <+> dataset <+> login

}

object Web {

  private val web = Reader[Env, Web](_.webserver)

  val uiRoot = web map { _.uiRoot }

  val dataset = web map { _.dataset }

  val upload = web map { _.upload }

  val health = web map { _.health }

  val search = web map { _.search }

  val login = web map { _.login }

  val static = web map { _.static }

  case class DefaultWeb(env: Env) extends Web {

    lazy val uiRoot = "../frontend/dist"

    lazy val dataset = DatasetController.instance(env)

    lazy val health = HealthCheckController.instance(env)

    lazy val search = SearchSequenceController.instance(env)

    lazy val upload = FileUploadController.instance(env)

    lazy val login = LoginController.instance(env)

    lazy val static = StaticContentController.instance(env)

  }
}
