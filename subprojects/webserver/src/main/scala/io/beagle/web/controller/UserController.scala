package io.beagle.web.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.domain.User
import io.beagle.persistence.PersistenceEnv
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.Http4sDsl

case object UserController {

  val PathName = "user"

  case class CreateUserRequest(username: String, password: String, email: String)

}

case class UserController(env: PersistenceEnv) extends Http4sDsl[IO] {

  import UserController._

  val route = HttpRoutes.of[IO] {
    case req@POST -> Root / PathName => req.decode[CreateUserRequest] { createUser =>
      env.userService.create(User(createUser.username, createUser.password, createUser.email))
        .transact(env.transactor)
      ???
    }
  }
}
