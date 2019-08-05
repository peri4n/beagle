package io.beagle.components

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseSettings {
  lazy val transactor = Transactor.fromDriverManager[IO](
    driver = driver,
    url = s"$protocol://$host:$port/$database",
    user = username,
    pass = password
  )

  def protocol: String

  def host: String

  def port: Int

  def database: String

  def username: String

  def password: String

  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  def driver: String
}

object DatabaseSettings {
  def transactor = Settings.database map { _.transactor }
}
