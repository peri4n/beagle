package io.beagle.security

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.domain.UserItem
import org.http4s.server.AuthMiddleware

//case class BasicAuth(authRealm: String, xa: Transactor[IO], userService: UserService) {
//
//  private def authenticator(credentials: BasicCredentials) =
//    userService.findByNameAndPassword(credentials.username, credentials.password).transact(xa)
//
//  val middleware: AuthMiddleware[IO, UserItem] = BAuth(authRealm, authenticator)
//
//}
