package io.beagle.components.settings

sealed trait DatabaseSettings {

  def protocol: String

  def host: String

  def port: Int

  def database: String

  def username: String

  def password: String

  def driver: String
}

object DatabaseSettings {

  case class LocalPostgre(port: Int,
                          database: String,
                          username: String,
                          password: String) extends DatabaseSettings {

    val protocol = "jdbc:postgresql"

    val host = "localhost"

    val driver = "org.postgresql.Driver"
  }

}
