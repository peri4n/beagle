package io.beagle.security

import cats.effect.IO
import cats.effect.concurrent.Ref

case class JwtTokenRepository(db: Ref[IO, Map[String, String]]) {

  def add(username: String, token: String): IO[Unit] = db.update(_ + ( username -> token ))

  def find(username: String, token: String): IO[Option[String]] = db.get map { _.get(username) }

  def delete(username: String): IO[Unit] = db.update(_ - username)

}
