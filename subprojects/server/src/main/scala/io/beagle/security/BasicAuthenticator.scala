package io.beagle.security

import cats.effect.IO
import doobie.implicits._
import io.beagle.Env
import io.beagle.components.settings.SecuritySettings
import io.beagle.components.{Service, Settings, Transaction}
import io.beagle.domain.UserItem
import io.beagle.service.UserService
import org.http4s.BasicCredentials
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.authentication.BasicAuth

case class BasicAuthenticator(securitySettings: SecuritySettings, transaction: Transaction, userService: UserService) {

  private def authenticator(credentials: BasicCredentials) =
    userService.findByNameAndPassword(credentials.username, credentials.password).transact(transaction.transactor)

  val middleware: AuthMiddleware[IO, UserItem] = BasicAuth(securitySettings.basicAuthRealm, authenticator)

}

object BasicAuthenticator {

  def instance =
    for {
      securitySettings <- Settings.security
      jdbc <- Env.transaction
      user <- Service.user
    } yield BasicAuthenticator(securitySettings, jdbc, user)
}
