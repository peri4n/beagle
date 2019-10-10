package io.beagle.security

import cats.data.OptionT
import cats.effect.IO
import doobie.implicits._
import io.beagle.components.Transaction
import io.beagle.domain.{UserId, UserItem}
import io.beagle.service.UserService
import tsec.authentication.IdentityStore

class UserIdentityStore(userService: UserService, transaction: Transaction) extends IdentityStore[IO, Int, UserItem] {

  def get(id: Int): OptionT[IO, UserItem] =
    OptionT(userService
      .findById(UserId(id))
      .transact(transaction.transactor))
}
