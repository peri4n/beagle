package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.{Controller, Service}
import io.beagle.domain.{User, UserItem}
import io.beagle.environments.TestEnv
import io.circe.generic.simple.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class LoginControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  val env = TestEnv.of[LoginControllerSpec]

  val userService = Service.user(env)

  val controller = Controller.login(env).orNotFound

  val xa = env.persistence.transactor

  "The LoginController" should {
    "return OK if the auth is successful" in {
      val user = User("admin", "admin", "")

      val testCase = for {
        _ <- userService.create(user).transact(xa)
        response <- controller.run(Request(
          method = Method.GET,
          uri = uri"/login",
          headers = Headers.of(
            Header("Authorization", "Basic YWRtaW46YWRtaW4=")
          ))
        )
      } yield response

      val response = runAwait(testCase)
      response must haveStatus(Status.Ok)
      response must haveBody { userItem: UserItem =>
        userItem.user must beEqualTo(user)
      }
    }

    "return 401 if caller doesn't provide basic auth headers" in {
      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/login")))

      response must haveStatus(Status.Unauthorized)
    }

    "return 401 if the provided user has a different password" in {
      val user = User("admin", "wrongPassword", "")

      val testCase = for {
        _ <- userService.create(user).transact(xa)
        response <- controller.run(Request(
          method = Method.GET,
          uri = uri"/login",
          headers = Headers.of(
            Header("Authorization", "Basic YWRtaW46YWRtaW4=")
          ))
        )
      } yield response

      val response = runAwait(testCase)
      response must haveStatus(Status.Unauthorized)
    }
  }
}
