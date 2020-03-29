package io.beagle.security

import java.time.LocalDateTime

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.User

case class TokenStore(settings: JwtConf, ref: Ref[IO, Map[Token, Session]] = Ref.unsafe(Map.empty)) {

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

