package io.beagle.components

import cats.effect.IO
import io.beagle.Env
import io.beagle.domain.UserItem
import io.beagle.security.{BasicAuth, JwtAuth}
import org.http4s.server.AuthMiddleware

sealed trait Security {

  def basicAuth: AuthMiddleware[IO, UserItem]

  def jwtAuth: JwtAuth

}

object Security {

  def basicAuth = BasicAuth.instance

  def jwtAuth = JwtAuth.instance

  case class DefaultSecurity(env: Env) extends Security {

    lazy val basicAuth = Security.basicAuth(env)

    lazy val jwtAuth = Security.jwtAuth(env)

  }

}
