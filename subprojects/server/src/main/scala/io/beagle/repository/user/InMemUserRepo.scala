package io.beagle.repository.user

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{User, UserId, UserItem}

case class InMemUserRepo(db: Ref[IO, Map[UserId, UserItem]], counter: Ref[IO, Long]) extends UserRepo {

  def create(user: User): IO[UserItem] =
    for {
      c <- counter.get
      _ <- counter.update(_ + 1)
      id = UserId(c)
      view = UserItem(id, user)
      _ <- db.update(store => store + ( id -> view ))
    } yield view

  def update(id: UserId, user: User): IO[UserItem] =
    db.modify(map => {
      val view = UserItem(id, user)
      (map.updated(id, view), view)
    })

  def findById(id: UserId): IO[Option[UserItem]] = db.get.map(_.get(id))

  def findByName(name: String): IO[Option[UserItem]] = db.get.map(_.values.find(_.user.name == name))

  def delete(id: UserId): IO[Unit] = db.update(map => map - id)

}

