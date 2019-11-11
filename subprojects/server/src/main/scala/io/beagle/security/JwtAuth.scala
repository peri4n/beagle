package io.beagle.security

import io.beagle.components.Settings
import io.beagle.components.settings.JwtSettings
import io.circe.syntax._
import pdi.jwt.{JwtAlgorithm, JwtCirce}

object JwtAuth {

  def instance = Settings.security map { settings => JwtAuth(settings.jwt) }

}

case class JwtAuth(settings: JwtSettings) {

  def generateToken(username: String): String = JwtCirce.encode(username.asJson, settings.secret, JwtAlgorithm.HS256)

}
