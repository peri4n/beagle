package io.beagle.web.api

import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

object UserApi {

  val PathName = "user"

  case class CreateUserRequest(username: String, password: String, email: String)

  case class CreateUserResponse(msg: String)

  val create = endpoint.post
    .description("Responsible for creating a new user.")
    .in(PathName)
    .in(jsonBody[CreateUserRequest])
    .out(jsonBody[CreateUserResponse])
    .errorOut(jsonBody[CreateUserResponse])


}
