package io.beagle.components.persistence

sealed trait PersistenceSettings {

  def protocol: String

  def host: String

  def port: Int

  def database: String

  def username: String

  def password: String

  def driver: String
}

object PersistenceSettings {

  case class LocalPostgre(database: String,
                          port: Int = 5432,
                          username: String = "fbull",
                          password: String = "password") extends PersistenceSettings {

    val protocol = "jdbc:postgresql"

    val host = "localhost"

    val driver = "org.postgresql.Driver"
  }

}
