package io.beagle.domain

case class UserId(id: Long) extends AnyVal

case class User(name: String, password: String, email: String)

case class UserItem(id: UserId, user: User)
