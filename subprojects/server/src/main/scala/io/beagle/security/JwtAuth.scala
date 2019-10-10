package io.beagle.security

import java.time.Clock

import cats.effect.IO
import io.beagle.components.Settings
import io.beagle.components.settings.JwtSettings
import io.circe.syntax._
import tsec.authentication.AugmentedJWT
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims
import tsec.mac.jca.HMACSHA256

import scala.concurrent.duration._

object JwtAuth {

  def instance = Settings.security

}

case class JwtAuth(settings: JwtSettings) {

  implicit val clock = Clock.systemUTC()

//  def generateToken(username: String): IO[AugmentedJWT[HMACSHA256, Int]] = {
//    for {
//      claims <- JWTClaims.withDuration[IO](
//        customFields = List("user" -> username.asJson),
//        expiration = Some(10.minutes))
//      key = HMACSHA256.unsafeBuildKey("secret".getBytes())
//      jwt <- JWTMac.build[IO, HMACSHA256](claims, key)
//    } yield jwt
//  }
}
