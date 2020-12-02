package io.beagle.security

import scala.concurrent.duration.FiniteDuration

case class JwtSettings(expirationTime: FiniteDuration, secret: String)
