package io.beagle.controller

import java.io.File

import cats.effect._
import io.beagle.Env
import io.beagle.components.{Execution, Web}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent._

import scala.concurrent.ExecutionContext.Implicits.global

object StaticContentController {

  def instance =
    for {
      execution <- Env.execution
      settings <- Web.uiRoot
    } yield StaticContentController(execution, settings).route

}

case class StaticContentController(execution: Execution, uiRoot: String) extends Http4sDsl[IO] {

  import execution._

  val route: HttpRoutes[IO] = fileService[IO](FileService.Config[IO](absolutePathOf(uiRoot), Blocker.liftExecutionContext(global)))

  private def absolutePathOf(dir: String) = new File(dir).getAbsolutePath
}
