package io.beagle.security

import cats.effect.IO
import io.beagle.components.settings.JwtSettings
import io.beagle.domain.UserId
import org.specs2.mutable.Specification

import scala.concurrent.duration._

class TokenStoreTest extends Specification {

  "TokenStore" should {
    "store new sessions" in {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, UserId(1))
        session <- tokenStore.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession should beSome
      maybeSession.get.inactiveFor() should be <= 1.seconds
    }

    "renew already existing sessions" in {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, UserId(1))
        firstSession <- tokenStore.sessionOf(randomHashValue)
        _ <- tokenStore.renew(randomHashValue)
        secondSession <- tokenStore.sessionOf(randomHashValue)
      } yield (firstSession, secondSession)

      val (firstSession, secondSession) = test.unsafeRunSync()

      (firstSession should beSome) and (secondSession should beSome)
      firstSession.get.lastActivity should be < secondSession.get.lastActivity
    }

    "not renew unknown sessions" in {
      val randomHashValue = "entariosniamcairesntmcarsnt"
      val unknownHashValue = "ianstioeanrstiearnstoianerstien"
      val tokenStore = TokenStore(JwtSettings(5 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, UserId(1))
        firstStore <- tokenStore.ref.get
        _ <- tokenStore.renew(unknownHashValue)
        secondStore <- tokenStore.ref.get
      } yield (firstStore, secondStore)

      val (firstStore, secondStore) = test.unsafeRunSync()

      firstStore should beEqualTo(secondStore)
    }

    "remove expired sessions" in {
      import scala.concurrent.ExecutionContext
      implicit val timer = IO.timer(ExecutionContext.global)

      val randomHashValue = "entariosniamcairesntmcarsnt"
      val tokenStore = TokenStore(JwtSettings(1 seconds, "secret"))

      val test = for {
        _ <- tokenStore.add(randomHashValue, UserId(1))
        _ <- IO.sleep(2 seconds)
        _ <- tokenStore.clear()
        session <- tokenStore.sessionOf(randomHashValue)
      } yield session

      val maybeSession = test.unsafeRunSync()

      maybeSession should beNone
    }

  }
}
