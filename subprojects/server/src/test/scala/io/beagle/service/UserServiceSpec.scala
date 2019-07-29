package io.beagle.service

import io.beagle.Generators._
import io.beagle.components.Services
import io.beagle.domain.User
import io.beagle.environments.TestEnv
import io.beagle.service.UserService.{UserAlreadyExists, UserDoesNotExist}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class UserServiceSpec extends FunSpec with GeneratorDrivenPropertyChecks with Matchers {

  describe("The UserService") {
    it("can create new users") {
      val environment = TestEnv.of[UserService]
      val service = Services.user(environment)

      forAll { user: User =>
        val item = service.create(user).unsafeRunSync().user
        item.name should be(user.name)
        item.password should be(user.password)
        item.email should be(user.email)
      }
    }

    it("fails to create if a user is already present") {
      val environment = TestEnv.of[UserService]
      val service = Services.user(environment)

      forAll { user: User =>
        val prog = for {
          _ <- service.create(user)
          _ <- service.create(user)
        } yield ()

        an[UserAlreadyExists] should be thrownBy ( prog.unsafeRunSync() )
      }
    }
    it("can delete a already present user") {
      val environment = TestEnv.of[UserService]
      val service = Services.user(environment)

      forAll { user: User =>
        val prog = for {
          _ <- service.create(user)
          _ <- service.delete(user)
        } yield ()

        prog.unsafeRunSync()
      }
    }
    it("fails to delete if a user is not present") {
      val environment = TestEnv.of[UserService]
      val service = Services.user(environment)

      forAll { user: User =>
        val prog = for {
          _ <- service.delete(user)
        } yield ()

        an[UserDoesNotExist] should be thrownBy ( prog.unsafeRunSync() )
      }
    }
  }
}
