package io.beagle.security

import cats.effect.IO

case class SecurityEnv(tokenStore: TokenStore)

object SecurityEnv {
  def from(settings: SecuritySettings): IO[SecurityEnv] =
    for {
      store <- TokenStore.empty(settings.jwt)
    } yield SecurityEnv(store)
}

