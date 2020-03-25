package io.beagle

import java.time.{Duration, LocalDateTime}

import io.beagle.domain.User

import scala.concurrent.duration.FiniteDuration
import scala.jdk.DurationConverters._

package object security {

  type Token = String

  case class JwtSettings(expirationTime: FiniteDuration, secret: String)

  case class UserSession(user: User, lastActivity: LocalDateTime) {

    def inactiveFor() = Duration.between(lastActivity, LocalDateTime.now()).toScala

    def refresh() = this.copy(lastActivity = LocalDateTime.now())
  }

}
