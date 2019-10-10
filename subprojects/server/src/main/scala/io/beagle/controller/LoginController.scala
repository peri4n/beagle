package io.beagle.controller

import cats.effect.IO
import io.beagle.components.settings.JwtSettings
import io.beagle.components.{Security, Service, Settings, Transaction}
import io.beagle.domain.UserItem
import io.beagle.service.UserService
import io.circe.generic.simple.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object LoginController {

  val PathName = "login"

  case class LoginRequest(username: String, password: String)

  def instance = for {
    security <- Security.basicAuth
  } yield LoginController().route
}

case class LoginController() extends Http4sDsl[IO] {

  import LoginController._


  val route = AuthedRoutes.of[UserItem, IO] {
    case request@PUT -> Root / PathName as user => request.req.decode[LoginRequest] { login =>
      val createToken = ???
      //      for {
      //        token <- JwtAuth(jwtSettings).generateToken(user.user.name)
      //        _ <- jwtSettings.tokenStore.put(token)
      //      } yield token

      Ok("")
    }
  }
}
