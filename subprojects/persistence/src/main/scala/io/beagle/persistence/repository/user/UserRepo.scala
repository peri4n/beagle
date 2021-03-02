package io.beagle.persistence.repository.user

import doobie.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}

trait UserRepo {

  def createTable(): ConnectionIO[Int]

  def create(user: User): ConnectionIO[UserItem]

  def update(id: UserId, user: User): ConnectionIO[UserItem]

  def findById(id: UserId): ConnectionIO[Option[UserItem]]

  def findByName(name: String): ConnectionIO[Option[UserItem]]

  def delete(id: UserId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}
