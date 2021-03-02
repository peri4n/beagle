package io.beagle.persistence.service

import doobie.implicits._
import io.beagle.domain.User
import io.beagle.persistence.testsupport.DbSuite

class UserServiceTest extends DbSuite {

  val userService = setup().userService

  val xa = setup().xa

  test("can create users") {
    val user = User("foo", "pw", "example@example.com")

    val test = userService.create(user)

    assertIOBoolean(test.transact(xa).map { _.user == user })
  }

  test("doesn't find not existing users") {
    val test = userService.findByName("name")

    assertIO(test.transact(xa), returns = None)
  }

  test("can delete users") {
    val user = User("name", "pw", "example@example.com")

    val test = for {
      item <- userService.create(user)
      _ <- userService.delete(item.user)
      user <- userService.findById(item.id)
    } yield user

    assertIO(test.transact(xa), returns = None)
  }

  test("can find a users by name") {
    val admin = User("admin", "pw", "admin@example.com")
    val analyst = User("analyst", "pw", "admin@example.com")

    val test = for {
      _ <- userService.create(analyst)
      _ <- userService.create(admin)
      user <- userService.findByName(admin.name)
    } yield user

    assertIOBoolean(test.transact(xa).map { maybeUser =>
      maybeUser.exists(_.user == admin)
    })
  }

  test("can update already existing users") {
    val user = User("name", "pw", "example@example.com")
    val newUser = user.copy(email = "change@example.com")

    val test = for {
      item <- userService.create(user)
      _ <- userService.update(item.user, newUser)
      user <- userService.findById(item.id)
    } yield user

    assertIOBoolean(test.transact(xa).map { maybeUser =>
      maybeUser.exists(_.user == newUser)
    })
  }
}
