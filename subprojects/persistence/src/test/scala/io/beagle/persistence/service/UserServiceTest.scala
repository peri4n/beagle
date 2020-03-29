package io.beagle.persistence.service

import doobie.implicits._
import io.beagle.domain.User
import io.beagle.persistence.service.UserService.UserAlreadyExists
import io.beagle.persistence.service.testsupport.PersistenceSupport
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class UserServiceTest extends PersistenceSupport with ScalaCheckPropertyChecks with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  override def beforeAll = {
    UserService.createTable().transact(xa).unsafeRunSync()
  }

  after {
    UserService.deleteAll().transact(xa).unsafeRunSync()
  }

  describe("A user service") {
    describe("can create users") {
      it("successfully if they are not already present") {
        val user = User("foo", "1234", "gna@example.com")
        UserService.create(user).transact(xa).unsafeRunSync().user should be(user)
      }
      it("unsuccessfully if a user is already present") {
        val user = User("foo1", "1234", "gna@example.com")
        val test = for {
          _ <- UserService.create(user).transact(xa)
          _ <- UserService.create(user).transact(xa)
        } yield ()

        an[UserAlreadyExists] should be thrownBy test.unsafeRunSync()
      }
    }

    //    "update users" in {
    //      "if they already exist" in {
    //        prop { user: User =>
    //          val test = for {
    //            userItem <- UserService.create(user).transact(xa)
    //            newItem <- UserService.update(userItem.user, user.copy(email = "foo@bar.com")).transact(xa)
    //          } yield newItem
    //
    //          runAwait(test).user shouldEqual (user.copy(email = "foo@bar.com"))
    //        }.after(UserService.deleteAll())
    //      }
    //    }
    //
    //    "delete users" in {
    //      "if they exist" in {
    //        prop { user: User =>
    //          val test = for {
    //            _ <- UserService.create(user).transact(xa)
    //            _ <- UserService.delete(user).transact(xa)
    //          } yield true
    //
    //          runAwait(test) shouldEqual true
    //        }.after(UserService.deleteAll())
    //      }
    //
    //      "unsuccessfully if the user is not present" in {
    //        prop { user: User =>
    //          val test = UserService.delete(user).transact(xa)
    //
    //          runAwait(test) should throwAn[UserDoesNotExist]
    //        }.after(UserService.deleteAll())
    //      }
}

}
