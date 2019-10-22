package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.{Controller, Service}
import io.beagle.domain
import io.beagle.environments.TestEnv
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.http4s.{Header, Headers, Method, Request, Status}
import org.specs2.mutable.Specification

class LoginControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  val env = TestEnv.of[LoginControllerSpec]

  val userService = Service.user(env)

  val controller = Controller.login(env).orNotFound

  val xa = env.persistence.transactor

  "The LoginController" should {
    "return OK if the auth is successful" in {
      val testCase = for {
        user <- userService.create(domain.User("admin", "admin", "")).transact(xa)
        response <- controller.run(Request(
          method = Method.GET,
          uri = uri"/login",
          headers = Headers.of(
            Header("Authorization", "Basic YWRtaW46YWRtaW4=")
          ))
        )
      } yield response

      val reponse = runAwait(testCase)
      pending
    }

    "return 401 if caller doesn't provide basic auth headers" in {
      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/login")))

      response must haveStatus(Status.Unauthorized)
      response must haveBody(())
    }
    "return 401 if the provided user has a different password" in {
      pending
    }
  }
}
