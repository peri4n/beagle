package io.beagle.components.settings

import scala.concurrent.duration.FiniteDuration

case class JwtSettings(expirationTime: FiniteDuration, secret: String)

