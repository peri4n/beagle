package io.beagle.persistence.repository

import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.UserRepo
import io.beagle.persistence.service.UserService
import io.beagle.persistence.testsupport.DbSupport
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, Tag, TagAnnotation}

object DatabaseTest extends Tag("DatabaseTest")

class UserRepoTest extends AnyFunSpec with Matchers with OptionValues with BeforeAndAfterEach with DbSupport {

  override def beforeEach(): Unit = {
    UserService.deleteAll().transact(xa).unsafeRunSync()
  }

  describe("A UserRepo") {
    it("can store users", DatabaseTest) {
      val user = User("name", "pw", "example@example.com")

      val test = for {
        item <- UserRepo.create(user)
        user <- UserRepo.findById(item.id)
      } yield user

      val maybeUser = test.transact(xa).unsafeRunSync()
      maybeUser.value shouldBe UserItem(UserId(1), user)
    }

    it("can delete users") {
      val user = User("name", "pw", "example@example.com")

      val test = for {
        item <- UserRepo.create(user)
        _ <- UserRepo.delete(item.id)
        user <- UserRepo.findById(item.id)
      } yield user

      val maybeUser = test.transact(xa).unsafeRunSync()
      maybeUser shouldBe None
    }

    it("can find a user by name") {
      val admin = User("admin", "pw", "admin@example.com")
      val analyst = User("analyst", "pw", "admin@example.com")

      val test = for {
        _ <- UserRepo.create(analyst)
        _ <- UserRepo.create(admin)
        user <- UserRepo.findByName(admin.name)
      } yield user

      val maybeUser = test.transact(xa).unsafeRunSync()
      maybeUser.value.user shouldBe admin
    }

    it("can update already existing users.") {
      val user = User("name", "pw", "example@example.com")
      val newUser = user.copy(email = "change@example.com")

      val test = for {
        item <- UserRepo.create(user)
        _ <- UserRepo.update(item.id, newUser)
        user <- UserRepo.findById(item.id)
      } yield user

      val maybeUser = test.transact(xa).unsafeRunSync()
      maybeUser.value.user shouldBe newUser
    }
  }
}
