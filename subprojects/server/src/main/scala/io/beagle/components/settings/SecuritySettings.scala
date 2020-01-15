package io.beagle.components.settings

import io.beagle.components.Security


sealed trait SecuritySettings {
  def basicAuthRealm: String

  def jwt: JwtSettings
}

object SecuritySettings {

  private val settings = Security.settings

  val jwt = settings map { _.jwt }

  case class DefaultSecuritySettings(basicAuthRealm: String, jwt:JwtSettings) extends SecuritySettings

}

