package io.beagle.repository.user

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{User, UserId, UserItem}

trait UserRepo {

  def create(user: User): IO[UserItem]

  def update(id: UserId, user: User): IO[UserItem]

  def findById(id: UserId): IO[Option[UserItem]]

  def findByName(name: String): IO[Option[UserItem]]

  def delete(id: UserId): IO[Unit]

}

object UserRepo {

  def inMemory =
    InMemUserRepo(
      Ref.unsafe[IO, Map[UserId, UserItem]](Map.empty),
      Ref.unsafe[IO, Long](1L)
    )

}
