package io.beagle.components

import cats.effect.{ContextShift, IO, Timer}

import scala.concurrent.ExecutionContext.Implicits.global

trait Settings {

  def uiRoot: String

  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  implicit val timer: Timer[IO] = IO.timer(global)

  def elasticSearch: ElasticSearchSettings

  def database: DatabaseSettings = new DatabaseSettings {

    val protocol: String = "jdbc:postgresql"

    val host: String = "localhost"

    val port: Int = 5432

    val username: String = "fbull"

    val password: String = "password"

    val driver: String = "org.postgresql.Driver"

    val database: String = "beagle"
  }

}

object Settings {

  val uiRoot = Env.settings map { _.uiRoot }

  val elasticSearch = Env.settings map { _.elasticSearch }

  val database = Env.settings map { _.database }

}
