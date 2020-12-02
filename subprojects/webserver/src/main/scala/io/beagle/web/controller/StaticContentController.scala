package io.beagle.web.controller

import java.io.File

import cats.effect._
import io.beagle.exec.Exec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent._

case class StaticContentController(execution: Exec, uiRoot: String) extends Http4sDsl[IO] {

  val route: HttpRoutes[IO] = {
    implicit val e = execution.threadPool
    fileService[IO](FileService.Config[IO](absolutePathOf(uiRoot), execution.blocker))
  }

  private def absolutePathOf(dir: String) = {
    new File(dir).getAbsolutePath
  }
}
