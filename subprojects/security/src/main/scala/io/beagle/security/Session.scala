package io.beagle.security

import java.time.{Duration, LocalDateTime}

import io.beagle.domain.User

import scala.jdk.DurationConverters._

case class Session(user: User, lastActivity: LocalDateTime) {

  def inactiveFor() = Duration.between(lastActivity, LocalDateTime.now()).toScala

  def refresh() = this.copy(lastActivity = LocalDateTime.now())
}
