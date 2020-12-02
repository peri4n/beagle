package io.beagle.security

import cats.effect.IO


case class SecuritySettings(jwt: JwtSettings) {

  def environment(): IO[SecurityEnv] = SecurityEnv.from(this)

}
