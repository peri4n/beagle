package io.beagle.security

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.beagle.Env
import io.beagle.components.{Security, Service}
import io.beagle.domain.UserItem
import io.beagle.service.UserService
import org.http4s.BasicCredentials
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.authentication.{BasicAuth => BAuth}

case class BasicAuth(authRealm: String, xa: Transactor[IO], userService: UserService) {

  private def authenticator(credentials: BasicCredentials) =
    userService.findByNameAndPassword(credentials.username, credentials.password).transact(xa)

  val middleware: AuthMiddleware[IO, UserItem] = BAuth(authRealm, authenticator)

}

object BasicAuth {

  def instance =
    for {
      securitySettings <- Security.settings
      jdbc <- Env.persistence
      user <- Service.user
    } yield BasicAuth(securitySettings.basicAuthRealm, jdbc.transactor, user).middleware
}
