package io.beagle.security

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.components.Security
import io.beagle.components.settings.JwtSettings
import io.beagle.domain.UserId

case class TokenStore(settings: JwtSettings, ref: Ref[IO, Map[Token, UserSession]]) {


  def add(token: Token, userId: UserId): IO[Unit] = {
    ref.update(_ + (token -> UserSession(userId, LocalDateTime.now())))
  }

  def renew(token: Token): IO[Unit] = {
    ref.update(store => store.get(token).fold(store) { session =>
      if (sessionExpired(session)) store else store + (token -> session.refresh())
    })
  }

  private[this] def sessionExpired(session: UserSession) = {
    session.inactiveFor() > settings.expirationTime
  }

  def sessionOf(token: Token): IO[Option[UserSession]] = ref.get.map(_.get(token))

  def clear(): IO[Unit] = ref.update(store => store.filterNot(record => sessionExpired(record._2)))
}

object TokenStore {
  def instance = Security.settings map { settings => TokenStore(settings.jwt) }


  def apply(settings: JwtSettings): TokenStore = TokenStore(settings, Ref.unsafe(Map.empty[Token, UserSession]))

}
