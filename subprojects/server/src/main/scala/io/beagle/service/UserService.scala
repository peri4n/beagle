package io.beagle.service

import cats.effect.Sync
import doobie.free.connection.ConnectionIO
import io.beagle.components.Repositories
import io.beagle.domain.{User, UserItem}
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

  def update(oldUser: User, newUser: User): ConnectionIO[UserItem] = {
    repo.findByName(oldUser.name).flatMap {
      case Some(userItem) => repo.update(userItem.id, newUser)
      case None           => Sync[ConnectionIO].raiseError[UserItem](UserDoesNotExist(oldUser))
    }
  }

  def delete(user: User): ConnectionIO[Unit] = {
    repo.findByName(user.name).flatMap {
      case Some(userItem) => repo.delete(userItem.id)
      case None           => Sync[ConnectionIO].raiseError[Unit](UserDoesNotExist(user))
    }
  }
}

object UserService {

  def instance = Repositories.user map { UserService(_) }

  case class UserAlreadyExists(user: User) extends Exception(user.name)

  case class UserDoesNotExist(user: User) extends Exception(user.name)

}
