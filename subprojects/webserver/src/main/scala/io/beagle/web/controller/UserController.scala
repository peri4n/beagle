package io.beagle.web.controller

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.domain.User
import io.beagle.persistence.service.UserService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

case class UserController(service: UserService, xa: Transactor[IO]) extends Http4sDsl[IO] with JsonSupport {

  import UserController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateUserRequest] { createUser =>
      Ok(service.create(User(createUser.username, createUser.password, createUser.email))
        .transact(xa))
    }
  }
}

case object UserController {

  val PathName = "user"

  case class CreateUserRequest(username: String, password: String, email: String)

}
