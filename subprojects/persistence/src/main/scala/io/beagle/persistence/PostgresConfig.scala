package io.beagle.persistence

import io.beagle.exec.Exec

case class DbCredentials(username: String, password: String)

case class PostgresConfig(host: String = "localhost",
                          port: Int = 5432,
                          database: String = "beagle",
                          credentials: DbCredentials = DbCredentials("beagle", "beagle"),
                          poolSize: Int = 5) {
  def environment(exec: Exec): Postgres = Postgres(this, exec)

  def jdbcUrl(): String = s"jdbc:postgresql://$host:$port/$database"
}
