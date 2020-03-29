package io.beagle.security

import io.beagle.domain.User
import io.circe.generic.auto._
import io.circe.parser._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, TryValues}

import scala.concurrent.duration._

class JwtTest extends AnyFunSpec with Matchers with TryValues with EitherValues {

  val jwt = Jwt(JwtConf(10 seconds, "secret"))

  import jwt._

  describe("JWT authentication") {
    it("should decode what was previously encoded") {
      val user = User("name", "password", "email")

      val Right(parseResult) = parse(decode(encode(user)).success.value.content)
      parseResult.as[User] shouldBe Right(user)
    }
  }

}
