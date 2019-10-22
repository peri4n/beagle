package io.beagle.components.persistence

sealed trait PersistenceSettings
case class LocalPostgre(database: String,
                        port: Int = 5432,
                        username: String = "fbull",
                        password: String = "password") extends PersistenceSettings {
  val protocol = "jdbc:postgresql"
  val host = "localhost"
  val driver = "org.postgresql.Driver"
}
case object InMemory extends PersistenceSettings
