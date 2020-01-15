package io.beagle.components

import cats.data.Reader
import cats.effect.IO
import io.beagle.Env
import io.beagle.components.settings.SecuritySettings.DefaultSecuritySettings
import io.beagle.components.settings.{JwtSettings, SecuritySettings}
import io.beagle.domain.UserItem
import io.beagle.security.{BasicAuth, JwtAuth, TokenStore}
import org.http4s.server.AuthMiddleware

import scala.concurrent.duration._

sealed trait Security {

  def settings: SecuritySettings

  def basicAuth: AuthMiddleware[IO, UserItem]

  def jwtAuth: JwtAuth

  def tokenStore: TokenStore
}

object Security {

  private val security = Reader[Env, Security](_.security)

  val settings = security map { _.settings}

  val basicAuth = security map { _.basicAuth }

  val jwtAuth = security map { _.jwtAuth }

  val tokenStore = security map { _.tokenStore }

  case class DefaultSecurity(env: Env) extends Security {

    lazy val settings = DefaultSecuritySettings("realm", JwtSettings(30.minutes, "secret"))

    lazy val basicAuth = BasicAuth.instance(env)

    lazy val jwtAuth = JwtAuth.instance(env)

    lazy val tokenStore: TokenStore = TokenStore.instance(env)
  }

}
