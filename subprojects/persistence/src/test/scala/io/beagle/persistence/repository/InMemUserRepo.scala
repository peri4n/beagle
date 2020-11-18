package io.beagle.persistence.repository

import cats.effect.concurrent.Ref
import cats.effect.{IO, LiftIO}
import doobie.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.UserRepository

class InMemUserRepo(db: Ref[IO, Map[UserId, UserItem]], counter: Ref[IO, Long]) extends UserRepository {

  override def createTable(): ConnectionIO[Int] = LiftIO[ConnectionIO].liftIO(
    db.set(Map.empty).map(_ => 1))

  override def create(user: User): ConnectionIO[UserItem] = LiftIO[ConnectionIO].liftIO(
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = UserId(c)
      view = UserItem(id, user)
      _ <- db.update(store => store + (id -> view))
    } yield view
  )

  override def update(id: UserId, user: User): ConnectionIO[UserItem] = LiftIO[ConnectionIO].liftIO(
    db.modify(map => {
      val view = UserItem(id, user)
      (map.updated(id, view), view)
    })
  )

  override def findById(id: UserId): ConnectionIO[Option[UserItem]] =
    LiftIO[ConnectionIO].liftIO(db.get.map(db => db.get(id)))

  override def findByName(name: String): ConnectionIO[Option[UserItem]] =
    LiftIO[ConnectionIO].liftIO(db.get.map(_.values.find(_.user.name == name)))

  override def delete(id: UserId): ConnectionIO[Unit] =
    LiftIO[ConnectionIO].liftIO(db.update(map => map - id))

  override def deleteAll(): ConnectionIO[Unit] =
    LiftIO[ConnectionIO].liftIO(db.update(_ => Map.empty))
}

object InMemUserRepo {

  def create(): IO[InMemUserRepo] =
    for {
      db <- Ref.of[IO, Map[UserId, UserItem]](Map.empty)
      counter <- Ref.of[IO, Long](1L)
    } yield new InMemUserRepo(db, counter)
}
