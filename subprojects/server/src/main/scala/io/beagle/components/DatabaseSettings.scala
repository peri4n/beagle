package io.beagle.components

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseSettings {

  def protocol: String

  def host: String

  def port: Int

  def database: String

  def username: String

  def password: String

  def driver: String

  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  lazy val transactor = Transactor.fromDriverManager[IO](
    driver = driver,
    url = s"$protocol://$host:$port/$database",
    user = username,
    pass = password
  )
}
 object DatabaseSettings {
   val transactor = Settings.database map { _.transactor }
 }
