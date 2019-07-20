package io.beagle.repository.user

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.domain.{User, UserId, UserItem}

class DbUserRepo(xa: Transactor[IO]) extends UserRepo {

  import DbUserRepo._

  def create(user: User): IO[UserItem] =
    sql"INSERT INTO $TableName (username, password) VALUES (${ user.name }, ${ user.password })".update
      .withUniqueGeneratedKeys[UserItem]("id", "identifier", "sequence")
      .transact(xa)

  def update(id: UserId, user: User): IO[UserItem] =
    sql"UPDATE $TableName SET name = ${ user.name }, password = ${ user.password } WHERE id = $id".update
      .withUniqueGeneratedKeys[UserItem]("id", "name", "password")
      .transact(xa)

  def findById(id: UserId): IO[Option[UserItem]] =
    sql"SELECT * FROM $TableName WHERE id = $id".query[UserItem]
      .option
      .transact(xa)

  def findByName(name: String): IO[Option[UserItem]] =
    sql"SELECT * FROM $TableName WHERE name = $name".query[UserItem]
      .option
      .transact(xa)

  def delete(id: UserId): IO[Unit] =
    sql"DELETE FROM $TableName WHERE id = $id".update
      .run
      .transact(xa)
      .map(_ => Unit)

}

object DbUserRepo {
  val TableName = "users"
}
