package io.beagle.persistence.repository

import cats.effect.LiftIO
import doobie.ConnectionIO
import doobie.implicits._
import io.beagle.domain.{User, UserId, UserItem}
import io.beagle.persistence.repository.user.InMemUserRepo
import io.beagle.persistence.testsupport.InMemSupport
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InMemUserRepoTest extends AnyFunSpec with Matchers with OptionValues with InMemSupport {

  describe("The InMemUserRepo") {
    it("can store users") {
      val user = User("foo", "secret", "email@example.net")
      val test = for {
        repo <- LiftIO[ConnectionIO].liftIO(InMemUserRepo.create())
        _ <- repo.create(user)
        user <- repo.findById(UserId(1))
      } yield user

      val userItem = test.transact(transactor).unsafeRunSync()
      userItem.value shouldBe UserItem(UserId(1), user)
    }

  }

}
