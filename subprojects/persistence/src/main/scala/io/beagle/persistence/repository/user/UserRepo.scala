package io.beagle.persistence.repository.user

import doobie.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}
import doobie.implicits.javatime._

case object UserRepo extends UserRepository {

  val TableName = "users"

  def createTable(): ConnectionIO[Int] = {
    sql"""CREATE TABLE IF NOT EXISTS users (
         | id serial PRIMARY KEY,
         | username VARCHAR(255) UNIQUE NOT NULL,
         | password VARCHAR(255),
         | email VARCHAR(255),
         | created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)""".stripMargin.update.run
  }

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
