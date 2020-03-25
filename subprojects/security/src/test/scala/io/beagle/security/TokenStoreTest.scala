package io.beagle.security

import cats.effect.IO
import io.beagle.domain.{User, UserId}
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class TokenStoreTest extends AnyFunSpec with Matchers with OptionValues {

  describe("A token store") {
    it("store new sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, User("name", "pw", "email"))
        session <- tokenStore.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession.value.inactiveFor() should be <= 1.seconds
    }

    it("renew already existing sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, User("name", "pw", "email"))
        firstSession <- tokenStore.sessionOf(randomHashValue)
        _ <- tokenStore.renew(randomHashValue)
        secondSession <- tokenStore.sessionOf(randomHashValue)
      } yield (firstSession, secondSession)

      val (firstSession, secondSession) = test.unsafeRunSync()

      firstSession.value.lastActivity should be < secondSession.value.lastActivity
    }

    it("not renew unknown sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val unknownHashValue = "ianstioeanrstiearnstoianerstien"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, User("name", "password", "email"))
        firstStore <- tokenStore.ref.get
        _ <- tokenStore.renew(unknownHashValue)
        secondStore <- tokenStore.ref.get
      } yield (firstStore, secondStore)

      val (firstStore, secondStore) = test.unsafeRunSync()

      firstStore should be(secondStore)
    }

    it("remove expired sessions") {
      import scala.concurrent.ExecutionContext
      implicit val timer = IO.timer(ExecutionContext.global)

      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(1 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, User("name", "password", "email"))
        _ <- IO.sleep(2 seconds)
        _ <- tokenStore.clear()
        session <- tokenStore.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession shouldBe empty
    }

  }
}
