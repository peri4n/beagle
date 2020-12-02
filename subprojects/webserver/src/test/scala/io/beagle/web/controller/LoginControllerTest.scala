package io.beagle.web.controller

import cats.effect.IO
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import org.scalatest.funspec.AnyFunSpec

class LoginControllerTest extends AnyFunSpec with ResponseMatchers {

  import LoginController._

  implicit val responseDecoder = jsonOf[IO, UserLoginResponse]

//  describe("The LoginController" ) {
//    it("return OK if the auth is successful" ) {
//      val user = User("admin", "admin", "")
//
//      val testCase = for {
//        _ <- userService.create(user).transact(xa)
//        response <- controller.run(Request(
//          method = Method.GET,
//          uri = uri"/login",
//          headers = Headers.of(
//            Header("Authorization", "Basic YWRtaW46YWRtaW4=") // admin:admin
//          ))
//        )
//      } yield response
//
//      val response = runAwait(testCase)
//      response must beEqualTo(Status.Ok) ^^ { r: Response[IO] => r.status }
//      response must haveBody { body: UserLoginResponse =>
//        body.jwtToken must beEqualTo(jwt.generateToken("admin"))
//      }
//    }
//
//    it("return 401 if caller doesn't provide basic auth headers" ) {
//      val response = runAwait(controller.run(Request(method = Method.GET, uri = uri"/login")))
//
//      response must haveStatus(Status.Unauthorized)
//    }
//
//    it("return 401 if the provided user has a different password" ) {
//      val user = User("wrongUser", "wrongPassword", "")
//
//      val testCase = for {
//        _ <- userService.create(user).transact(xa)
//        response <- controller.run(Request(
//          method = Method.GET,
//          uri = uri"/login",
//          headers = Headers.of(
//            Header("Authorization", "Basic d3JvbmdVc2VyOmZvbw==") // wrongUser:foo
//          ))
//        )
//      } yield response
//
//      val response = runAwait(testCase)
//      response must haveStatus(Status.Unauthorized)
//    }
//  }
}
