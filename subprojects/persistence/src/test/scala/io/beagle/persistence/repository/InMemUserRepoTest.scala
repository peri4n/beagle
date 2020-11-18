package io.beagle.persistence.repository

import java.sql.Connection

import cats.effect.{Blocker, ContextShift, IO, LiftIO, Resource}
import doobie.implicits._
import doobie.util.transactor.Strategy
import doobie.{ConnectionIO, KleisliInterpreter, Transactor}
import io.beagle.domain.{User, UserId, UserItem}
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class InMemUserRepoTest extends AnyFunSpec with Matchers with OptionValues {

  describe("The InMemUserRepo") {
    it("can store users") {
      implicit val ioContextShift: ContextShift[IO] =
        IO.contextShift(ExecutionContext.global)

      val transactor: Transactor[IO] = Transactor(
        (),
        (_: Unit) => Resource.pure[IO, Connection](null),
        KleisliInterpreter[IO](
          Blocker.liftExecutionContext(ExecutionContext.global)
        ).ConnectionInterpreter,
        Strategy.void
      )

      val user = User("foo", "secret", "email@example.net")
      val test = for {
        repo <- LiftIO[ConnectionIO].liftIO(InMemUserRepo.create())
        _ <- repo.create(user)
        user <- repo.findById(UserId(1))
      } yield user

      val userItem = test.transact(transactor).unsafeRunSync()
      userItem.value shouldBe UserItem(UserId(1), user)
    }

    it("can retrieve users") {

    }
  }

}
