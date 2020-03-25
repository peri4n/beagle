package io.beagle.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.Env.TestEnv
import io.beagle.components.Web
import io.beagle.domain.User
import io.circe.generic.auto._
import io.beagle.testsupport.ResponseMatchers
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.implicits._
import org.specs2.mutable.Specification

class LoginControllerSpec extends Specification with ResponseMatchers {

  import LoginController._

  implicit val responseDecoder = jsonOf[IO, UserLoginResponse]

  val setup = for {
    env <- TestEnv.of[LoginControllerSpec]
    userService = Service.user(env)
    jwt = Security.jwtAuth(env)
    controller = Web.login(env).orNotFound
  } yield (env, userService, jwt, controller)

  val (environment, userService, jwt, controller) = setup.unsafeRunSync()

  val xa = environment.persistence.transactor

  "The LoginController" should {
    "return OK if the auth is successful" in {
      val user = User("admin", "admin", "")

      val testCase = for {
        _ <- userService.create(user).transact(xa)
        response <- controller.run(Request(
          method = Method.GET,
          uri = uri"/login",
          headers = Headers.of(
            Header("Authorization", "Basic YWRtaW46YWRtaW4=") // admin:admin
          ))
        )
      } yield response

      val response = runAwait(testCase)
      response must beEqualTo(Status.Ok) ^^ { r: Response[IO] => r.status }
      response must haveBody { body: UserLoginResponse =>
        body.jwtToken must beEqualTo(jwt.generateToken("admin"))
      }
    }

    "return 401 if caller doesn't provide basic auth headers" in {
      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/login")))

      response must haveStatus(Status.Unauthorized)
    }

    "return 401 if the provided user has a different password" in {
      val user = User("wrongUser", "wrongPassword", "")

      val testCase = for {
        _ <- userService.create(user).transact(xa)
        response <- controller.run(Request(
          method = Method.GET,
          uri = uri"/login",
          headers = Headers.of(
            Header("Authorization", "Basic d3JvbmdVc2VyOmZvbw==") // wrongUser:foo
          ))
        )
      } yield response

      val response = runAwait(testCase)
      response must haveStatus(Status.Unauthorized)
    }
  }
}
