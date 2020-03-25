package io.beagle.controller

import cats.effect.IO
import io.beagle.domain.{UserId, UserItem}
import io.beagle.security.JwtAuth
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.slf4j.LoggerFactory

object LoginController {

  val PathName = "login"

  case class UserLoginResponse(userId: UserId, jwtToken: String)

  def instance = for {
    security <- Security.basicAuth
    jwt <- Security.jwtAuth
  } yield LoginController(security, jwt).route
}

case class LoginController(basicAuth: AuthMiddleware[IO, UserItem], jwtAuth: JwtAuth) extends Http4sDsl[IO] {

  import LoginController._

  private val Logger = LoggerFactory.getLogger(classOf[LoginController])

  val route: HttpRoutes[IO] = basicAuth(AuthedRoutes.of[UserItem, IO] {
    case GET -> Root / PathName as userItem =>
      Logger.info(s"${ userItem.user } just logged in")
      Ok(UserLoginResponse(userItem.id, jwtAuth.generateToken(userItem.user.name)))
  })
}
