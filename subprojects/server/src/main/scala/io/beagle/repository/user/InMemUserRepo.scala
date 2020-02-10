package io.beagle.repository.user

import cats.effect.concurrent.Ref
import cats.effect.{Async, IO}
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}

case class InMemUserRepo(db: Ref[IO, Map[UserId, UserItem]], counter: Ref[IO, Long]) extends UserRepo {

  def create(user: User): ConnectionIO[UserItem] =
    Async[ConnectionIO].liftIO(
      for {
        c <- counter.get
        _ <- counter.update(_ + 1)
        id = UserId(c)
        view = UserItem(id, user)
        _ <- db.update(store => store + (id -> view))
      } yield view)

  def update(id: UserId, user: User): ConnectionIO[UserItem] =
    Async[ConnectionIO].liftIO(
      db.modify(map => {
        val view = UserItem(id, user)
        (map.updated(id, view), view)
      }))

  def findById(id: UserId): ConnectionIO[Option[UserItem]] =
    Async[ConnectionIO].liftIO(db.get.map(_.get(id)))

  def findByName(name: String): ConnectionIO[Option[UserItem]] =
    Async[ConnectionIO].liftIO(db.get.map(_.values.find(_.user.name == name)))

  def delete(id: UserId): ConnectionIO[Unit] =
    Async[ConnectionIO].liftIO(db.update(map => map - id))

  def deleteAll(): ConnectionIO[Unit] =
    Async[ConnectionIO].liftIO(db.update(_ => Map.empty))
}

