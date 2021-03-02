package io.beagle.persistence.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.UserRepo

case class UserService(userRepo: UserRepo) {

  def createTable(): ConnectionIO[Int] = userRepo.createTable()

  def create(user: User): ConnectionIO[UserItem] = {
    userRepo.findByName(user.name).flatMap { maybeUser =>
      maybeUser.fold(userRepo.create(user)) { item =>
        Sync[ConnectionIO].raiseError(UserAlreadyExists(item))
      }
    }
  }

  def findById(id: UserId): ConnectionIO[Option[UserItem]] = userRepo.findById(id)

  def findByIdStrict(id: UserId): ConnectionIO[UserItem] =
    userRepo.findById(id).flatMap { maybeUser =>
      maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist("foo"))) { owner =>
        Sync[ConnectionIO].pure(owner)
      }
    }

  def findByName(userName: String): ConnectionIO[Option[UserItem]] = userRepo.findByName(userName)

  def findByNameAndPassword(userName: String, password: String): ConnectionIO[Option[UserItem]] =
    userRepo.findByName(userName).map {
      case Some(item) if item.user.password == password => Some(item)
      case _ => None
    }

  def findByNameStrict(userName: String): ConnectionIO[UserItem] = userRepo.findByName(userName).flatMap { maybeUser =>
    maybeUser.fold(Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(userName))) { owner =>
      Sync[ConnectionIO].pure(owner)
    }
  }

  def update(oldUser: User, newUser: User): ConnectionIO[UserItem] = {
    userRepo.findByName(oldUser.name).flatMap {
      case Some(userItem) => userRepo.update(userItem.id, newUser)
      case _ => Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(oldUser.name))
    }
  }

  def delete(user: User): ConnectionIO[Unit] = {
    userRepo.findByName(user.name).flatMap {
      case Some(userItem) => userRepo.delete(userItem.id)
      case _ => Sync[ConnectionIO].raiseError[Unit](UserDoesNotExist(user.name))
    }
  }

  def deleteAll(): ConnectionIO[Unit] = userRepo.deleteAll()

  case class UserAlreadyExists(item: UserItem) extends Exception(item.user.name)

  case class UserDoesNotExist(user: String) extends Exception(user)

}
