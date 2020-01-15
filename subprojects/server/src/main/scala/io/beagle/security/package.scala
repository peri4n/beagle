package io.beagle

import java.time.{Duration, LocalDateTime}

import io.beagle.domain.UserId
import scala.jdk.DurationConverters._

package object security {

  type Token = String

  case class UserSession(userId: UserId, lastActivity: LocalDateTime) {

    def inactiveFor() = Duration.between(lastActivity, LocalDateTime.now()).toScala

    def refresh() = this.copy(lastActivity = LocalDateTime.now())
  }

}
