package io.beagle.persistence.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.UserRepo

case object UserService {

  def createTable(): ConnectionIO[Int] = UserRepo.createTable()

  def create(user: User): ConnectionIO[UserItem] = {
    UserRepo.findByName(user.name).flatMap { maybeUser =>
      maybeUser.fold(UserRepo.create(user)) { _ =>
        Sync[ConnectionIO].raiseError(UserAlreadyExists(user))
      }
    }
  }

  def findById(id: UserId): ConnectionIO[Option[UserItem]] = UserRepo.findById(id)

  def findByIdStrict(id: UserId): ConnectionIO[UserItem] = UserRepo.findById(id).flatMap { maybeUser =>
    maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist("foo"))) { owner =>
      Sync[ConnectionIO].pure(owner)
    }
  }

  def findByName(userName: String): ConnectionIO[Option[UserItem]] = UserRepo.findByName(userName)

  def findByNameAndPassword(userName: String, password: String): ConnectionIO[Option[UserItem]] = UserRepo.findByName(userName).map {
    case Some(item) if item.user.password == password => Some(item)
    case _                                            => None
  }

  def findByNameStrict(userName: String): ConnectionIO[UserItem] = UserRepo.findByName(userName).flatMap { maybeUser =>
    maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(userName))) { owner =>
      Sync[ConnectionIO].pure(owner)
    }
  }

  def update(oldUser: User, newUser: User): ConnectionIO[UserItem] = {
    UserRepo.findByName(oldUser.name).flatMap {
      case Some(userItem) => UserRepo.update(userItem.id, newUser)
      case _              => Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(oldUser.name))
    }
  }

  def delete(user: User): ConnectionIO[Unit] = {
    UserRepo.findByName(user.name).flatMap {
      case Some(userItem) => UserRepo.delete(userItem.id)
      case _              => Sync[ConnectionIO].raiseError[Unit](UserDoesNotExist(user.name))
    }
  }

  def deleteAll(): ConnectionIO[Unit] = UserRepo.deleteAll()

  case class UserAlreadyExists(user: User) extends Exception(user.name)

  case class UserDoesNotExist(user: String) extends Exception(user)

}
