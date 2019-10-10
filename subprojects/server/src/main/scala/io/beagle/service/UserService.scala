package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.Repository
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.repository.user.UserRepo

case class UserService(repo: UserRepo) {

  import UserService._

  def create(user: User): ConnectionIO[UserItem] = {
    repo.findByName(user.name).flatMap { maybeUser =>
      maybeUser.fold(repo.create(user)) { _ =>
        Sync[ConnectionIO].raiseError(UserAlreadyExists(user))
      }
    }
  }

  def findById(id: UserId): ConnectionIO[Option[UserItem]] = repo.findById(id)

  def findByIdStrict(id: UserId): ConnectionIO[UserItem] = repo.findById(id).flatMap { maybeUser =>
    maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist("foo"))) { owner =>
      Sync[ConnectionIO].pure(owner)
    }
  }

  def findByName(userName: String): ConnectionIO[Option[UserItem]] = repo.findByName(userName)

  def findByNameAndPassword(userName: String, password: String): ConnectionIO[Option[UserItem]] = repo.findByName(userName).map {
    case Some(item) if item.user.password == password => Some(item)
    case None => None
  }

  def findByNameStrict(userName: String): ConnectionIO[UserItem] = repo.findByName(userName).flatMap { maybeUser =>
    maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(userName))) { owner =>
      Sync[ConnectionIO].pure(owner)
    }
  }

  def update(oldUser: User, newUser: User): ConnectionIO[UserItem] = {
    repo.findByName(oldUser.name).flatMap {
      case Some(userItem) => repo.update(userItem.id, newUser)
      case None           => Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(oldUser.name))
    }
  }

  def delete(user: User): ConnectionIO[Unit] = {
    repo.findByName(user.name).flatMap {
      case Some(userItem) => repo.delete(userItem.id)
      case None           => Sync[ConnectionIO].raiseError[Unit](UserDoesNotExist(user.name))
    }
  }

  def deleteAll(): ConnectionIO[Unit] = repo.deleteAll()
}

object UserService {

  def instance = Repository.user map { UserService(_) }

  case class UserAlreadyExists(user: User) extends Exception(user.name)

  case class UserDoesNotExist(user: String) extends Exception(user)

}
