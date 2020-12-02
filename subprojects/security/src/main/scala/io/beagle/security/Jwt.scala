package io.beagle.security

import io.beagle.domain.User
import io.circe.syntax._
import io.circe.generic.auto._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.Try

case class Jwt(settings: JwtSettings) {

  def encode(user: User): String = JwtCirce.encode(user.asJson, settings.secret, JwtAlgorithm.HS256)

  def decode(token: Token): Try[JwtClaim] = JwtCirce.decode(token, settings.secret, List(JwtAlgorithm.HS256))

}
