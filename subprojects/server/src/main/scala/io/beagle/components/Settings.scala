package io.beagle.components

trait Settings {

  def uiRoot: String

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

  def uiRoot = Env.settings map { _.uiRoot }

  def elasticSearch = Env.settings map { _.elasticSearch }

  def database = Env.settings map { _.database }

}
