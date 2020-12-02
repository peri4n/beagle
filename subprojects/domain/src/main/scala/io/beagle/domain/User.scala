package io.beagle.domain

import java.time.LocalDateTime

case class UserId(id: Long) extends AnyVal

case class User(name: String, password: String, email: String, created: LocalDateTime = LocalDateTime.now())

case class UserItem(id: UserId, user: User)
