package io.beagle.controller

import cats.effect._
import io.beagle.components.Settings
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent._

import scala.concurrent.ExecutionContext

object StaticContentController {

  val instance = Settings.uiRoot map { StaticContentController(_).route }

}

case class StaticContentController(uiRoot: String) extends Http4sDsl[IO] {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val route: HttpRoutes[IO] = fileService[IO](FileService.Config[IO](uiRoot))

}
