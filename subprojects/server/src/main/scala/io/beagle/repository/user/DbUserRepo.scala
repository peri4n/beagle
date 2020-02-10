package io.beagle.repository.user

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.domain.metas._

case object DbUserRepo extends UserRepo {

  val TableName = "users"

  def create(user: User): ConnectionIO[UserItem] = {
    sql"""INSERT INTO users (username, password, email, created)
          VALUES (${ user.name }, ${ user.password }, ${ user.email }, ${ user.created })"""
      .update
      .withUniqueGeneratedKeys[UserItem]("id", "username", "password", "email", "created")
  }

  def update(id: UserId, user: User): ConnectionIO[UserItem] =
    sql"""UPDATE users
          SET username = ${ user.name }, password = ${ user.password }, email = ${ user.email }, created = ${ user.created }
          WHERE id = $id""".update
      .withUniqueGeneratedKeys[UserItem]("id", "username", "password", "email", "created")

  def findById(id: UserId): ConnectionIO[Option[UserItem]] =
    sql"SELECT * FROM users WHERE id = $id".query[UserItem]
      .option

  def findByName(name: String): ConnectionIO[Option[UserItem]] =
    sql"SELECT * FROM users WHERE username = $name".query[UserItem]
      .option

  def delete(id: UserId): ConnectionIO[Unit] =
    sql"DELETE FROM users WHERE id = $id".update
      .run
      .map(_ => ())

  def deleteAll(): ConnectionIO[Unit] =
    sql"DELETE FROM users".update
      .run
      .map(_ => ())
}
