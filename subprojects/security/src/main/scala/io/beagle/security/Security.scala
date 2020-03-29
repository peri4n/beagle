package io.beagle.security

import cats.data.Reader

import scala.concurrent.duration.FiniteDuration

case class Security(realm: String, jwt: JwtConf) {

  lazy val tokenStore = TokenStore(jwt)

}

case class JwtConf(expirationTime: FiniteDuration, secret: String)

object Security {

  val security = Reader[Security, Security](identity)

  def tokenStore = security map { s => s.tokenStore }

}
