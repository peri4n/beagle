package io.beagle.repository.user

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}

case object DbUserRepo extends UserRepo {

  val TableName = "users"

  def create(user: User): ConnectionIO[UserItem] =
    sql"INSERT INTO $TableName (username, password) VALUES (${ user.name }, ${ user.password })".update
      .withUniqueGeneratedKeys[UserItem]("id", "identifier", "sequence")

  def update(id: UserId, user: User): ConnectionIO[UserItem] =
    sql"UPDATE $TableName SET name = ${ user.name }, password = ${ user.password } WHERE id = $id".update
      .withUniqueGeneratedKeys[UserItem]("id", "name", "password")

  def findById(id: UserId): ConnectionIO[Option[UserItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[UserItem]
      .option

  def findByName(name: String): ConnectionIO[Option[UserItem]] =
    sql"SELECT * FROM $TableName WHERE name = $name".query[UserItem]
      .option

  def delete(id: UserId): ConnectionIO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .map(_ => ())

  def deleteAll(): ConnectionIO[Unit] =
    sql"DELETE FROM $TableName".update
      .run
      .map(_ => ())
}
