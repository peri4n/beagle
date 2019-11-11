package io.beagle.controller

import io.circe.generic.simple.auto._
import org.http4s.circe.CirceEntityDecoder._
import cats.effect.IO
import io.beagle.components.Service
import io.beagle.domain.User
import io.beagle.service.UserService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

case object UserController {

  val PathName = "user"

  case class CreateUserRequest(username: String, password: String, email: String)

  def instance = Service.user map { UserController(_).route }
}

case class UserController(userService: UserService) extends Http4sDsl[IO] {

  import UserController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateUserRequest] { createUser =>
      userService.create(User(createUser.username, createUser.password, createUser.email))
      ???
    }
  }
}
