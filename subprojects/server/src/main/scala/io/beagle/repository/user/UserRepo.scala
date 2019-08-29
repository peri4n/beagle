package io.beagle.repository.user

import cats.effect.IO
import cats.effect.concurrent.Ref
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}

trait UserRepo {

  def create(user: User): ConnectionIO[UserItem]

  def update(id: UserId, user: User): ConnectionIO[UserItem]

  def findById(id: UserId): ConnectionIO[Option[UserItem]]

  def findByName(name: String): ConnectionIO[Option[UserItem]]

  def delete(id: UserId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}

object UserRepo {

  def inMemory =
    InMemUserRepo(
      Ref.unsafe[IO, Map[UserId, UserItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )

  def inDB = DbUserRepo

}
