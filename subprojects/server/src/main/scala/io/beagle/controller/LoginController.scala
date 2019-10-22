package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Security
import io.beagle.domain.UserItem
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import org.slf4j.LoggerFactory

object LoginController {

  val PathName = "login"

  def instance = for {
    security <- Security.basicAuth
  } yield LoginController(security).route
}

case class LoginController(authenticator: AuthMiddleware[IO, UserItem]) extends Http4sDsl[IO] {

  import LoginController._

  private val Logger = LoggerFactory.getLogger(classOf[LoginController])

  val route: HttpRoutes[IO] = authenticator(AuthedRoutes.of[UserItem, IO] {
    case request@PUT -> Root / PathName as user =>
      Logger.info(request.toString)
      Logger.info(user.toString)
      Ok("")
  })
}
