package io.beagle.security

import io.beagle.components.settings.{JwtSettings, SecuritySettings}
import io.circe.syntax._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.Try

object JwtAuth {

  def instance = SecuritySettings.jwt map { JwtAuth(_) }

}

case class JwtAuth(settings: JwtSettings) {

  def generateToken(username: String): String = JwtCirce.encode(username.asJson, settings.secret, JwtAlgorithm.HS256)

  def decodeToken(token: String): Try[JwtClaim] = JwtCirce.decode(token, settings.secret, List(JwtAlgorithm.HS256))

}
