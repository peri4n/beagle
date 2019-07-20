package io.beagle.service

import cats.effect.IO
import io.beagle.domain.{User, UserItem}
import io.beagle.repository.user.UserRepo

case class UserService(repo: UserRepo) {

  import UserService._

  def create(user: User): IO[UserItem] = {
    repo.findByName(user.name).flatMap { maybeUser =>
      maybeUser.fold(repo.create(user)) { _ =>
        IO.raiseError(UserAlreadyExists(user))
      }
    }
  }

  def delete(user: User): IO[Unit] = {
    repo.findByName(user.name).flatMap {
      case Some(userItem) => repo.delete(userItem.id)
      case None => IO.raiseError[Unit](UserDoesNotExist(user))
    }
  }
}

object UserService {

  case class UserAlreadyExists(user: User) extends Exception(user.name)

  case class UserDoesNotExist(user: User) extends Exception(user.name)

}
