package io.beagle.security

import io.beagle.domain.User
import munit.FunSuite

import scala.concurrent.duration._

class JwtTest extends FunSuite {

  val jwt = Jwt(JwtSettings(10 seconds, "secret"))

  import jwt._

  test("should decode what was previously encoded") {
    val user = User("name", "password", "email")

    assert(decode(encode(user)).isSuccess)
  }

}
