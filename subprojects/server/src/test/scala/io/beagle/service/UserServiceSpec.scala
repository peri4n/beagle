package io.beagle.service

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Generators._
import io.beagle.components.Service
import io.beagle.domain.User
import io.beagle.environments.TestEnv
import io.beagle.service.UserService.{UserAlreadyExists, UserDoesNotExist}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class UserServiceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks with Matchers with BeforeAndAfter {

  val environment = TestEnv.of[UserServiceSpec]

  val service = Service.user(environment)

  def run[A](cio: ConnectionIO[A]): A = {
    cio.transact(environment.persistence.transactor).unsafeRunSync()
  }

  after {
    run(service.deleteAll())
  }

  describe("Creating users") {
    it("can create new users") {
      forAll { user: User =>
        run(service.create(user)).user should be(user)
      }
    }

    it("fails to create if a user is already present") {
      forAll { user: User =>
        val test = for {
          _ <- service.create(user)
          _ <- service.create(user)
        } yield ()

        an[UserAlreadyExists] should be thrownBy run(test)
      }
    }
  }

  describe("Updating users") {
    it("can update existing users") {
      forAll { user: User =>
        val test = for {
          _ <- service.create(user)
          newItem <- service.update(user, user.copy(email = "foo@bar.com"))
        } yield newItem

        run(test).user should be(user.copy(email = "foo@bar.com"))
      }
    }
  }

  it("can delete an already present user") {
    forAll { user: User =>
      val test = for {
        _ <- service.create(user)
        _ <- service.delete(user)
      } yield ()

      run(test)
    }
  }

  it("fails to delete if the user is not present") {
    forAll { user: User =>
      val test = service.delete(user)

      an[UserDoesNotExist] should be thrownBy run(test)
    }
  }
}
