package io.beagle.security

import cats.effect.IO
import io.beagle.domain.User
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class TokenStoreTest extends AnyFunSpec with Matchers with OptionValues {

  describe("A token store") {
    it("store new sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"

      val test = for {
        store <- TokenStore.empty(JwtSettings(5 seconds, "secret"))
        _ <- store.add(randomHashValue, User("name", "pw", "email"))
        session <- store.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession.value.inactiveFor() should be <= 1.seconds
    }

    it("renew already existing sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"

      val test = for {
        store <- TokenStore.empty(JwtSettings(5 seconds, "secret"))
        _ <- store.add(randomHashValue, User("name", "pw", "email"))
        firstSession <- store.sessionOf(randomHashValue)
        _ <- store.renew(randomHashValue)
        secondSession <- store.sessionOf(randomHashValue)
      } yield (firstSession, secondSession)

      val (firstSession, secondSession) = test.unsafeRunSync()

      firstSession.value.lastActivity should be < secondSession.value.lastActivity
    }

    it("not renew unknown sessions") {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val unknownHashValue = "ianstioeanrstiearnstoianerstien"

      val test = for {
        store <- TokenStore.empty(JwtSettings(5 seconds, "secret"))
        _ <- store.add(randomHashValue, User("name", "password", "email"))
        firstStore <- store.ref.get
        _ <- store.renew(unknownHashValue)
        secondStore <- store.ref.get
      } yield (firstStore, secondStore)

      val (firstStore, secondStore) = test.unsafeRunSync()

      firstStore should be(secondStore)
    }

    it("remove expired sessions") {
      import scala.concurrent.ExecutionContext
      implicit val timer = IO.timer(ExecutionContext.global)

      val randomHashValue = "entariosniamcairesntmcarsnt"

      val test = for {
        store <- TokenStore.empty(JwtSettings(1 seconds, "secret"))
        _ <- store.add(randomHashValue, User("name", "password", "email"))
        _ <- IO.sleep(2 seconds)
        _ <- store.clear()
        session <- store.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession shouldBe None
    }

  }
}
