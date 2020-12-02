package io.beagle.security

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.User

import java.time.LocalDateTime

case class TokenStore(settings: JwtSettings, ref: Ref[IO, Map[Token, Session]]) {

  def add(token: Token, user: User): IO[Unit] = {
    ref.update(_ + (token -> Session(user, LocalDateTime.now())))
  }

  def renew(token: Token): IO[Unit] = {
    ref.update(store => store.get(token).fold(store) { session =>
      if (sessionExpired(session)) store else store + (token -> session.refresh())
    })
  }

  private[this] def sessionExpired(session: Session) = {
    session.inactiveFor() > settings.expirationTime
  }

  def sessionOf(token: Token): IO[Option[Session]] = ref.get.map(_.get(token))

  def clear(): IO[Unit] = ref.update(store => store.filterNot(record => sessionExpired(record._2)))
}

object TokenStore {

  def empty(settings: JwtSettings): IO[TokenStore] =
    Ref[IO].of(Map.empty[Token, Session]).map(store => TokenStore(settings, store))
}
