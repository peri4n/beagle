package io.beagle.components

import cats.effect.IO
import io.beagle.Env
import io.beagle.domain.UserItem
import io.beagle.security.BasicAuthenticator
import org.http4s.server.AuthMiddleware

trait Security {

  def basicAuth: AuthMiddleware[IO, UserItem]

}

object Security {

  def basicAuth =
    for {
      securitySettings <- Settings.security
      jdbc <- Env.transaction
      userService <- Service.user
    } yield BasicAuthenticator(securitySettings, jdbc, userService)

}
