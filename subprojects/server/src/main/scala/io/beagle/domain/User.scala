package io.beagle.domain

import java.time.ZonedDateTime

case class UserId(val id: Long) extends AnyVal

case class User(name: String, password: String, email: String, created: ZonedDateTime = ZonedDateTime.now())

case class UserItem(id: UserId, user: User)

