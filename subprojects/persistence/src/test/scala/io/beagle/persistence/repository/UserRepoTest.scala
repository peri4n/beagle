package io.beagle.persistence.repository

import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.UserRepo
import io.beagle.persistence.testsupport.PostgresSupport
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}

class UserRepoTest extends AnyFunSpec with Matchers with OptionValues with BeforeAndAfterAll with BeforeAndAfterEach with PostgresSupport {

  override def beforeAll(): Unit = {
    container.start()
    val dbSetup = for {
      _ <- environment.userService.createTable().transact(xa)
    } yield ()
    dbSetup.unsafeRunSync()
  }

  override def beforeEach(): Unit = environment.userService.deleteAll().transact(xa).unsafeRunSync()

  describe("A UserRepo") {
    it("can store users") {
      val user = User("name", "pw", "example@example.com")

      val test = for {
        item <- UserRepo.create(user)
        user <- UserRepo.findById(item.id)
      } yield user

      val maybeUser = test.transact(xa).unsafeRunSync()
      maybeUser.value shouldBe UserItem(UserId(1), user)
    }
  }

  override def afterAll(): Unit = {
    container.stop()
  }

}
