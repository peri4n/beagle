package io.beagle.persistence

import cats.effect.IO
import io.beagle.exec.Exec

sealed trait DbConfig {

  def environment(exec: Exec): IO[DB]

}

case class PostgresConfig(database: String,
                          user: String,
                          password: String,
                          host: String = "localhost",
                          port: Int = 5432,
                          poolSize: Int = 5) extends DbConfig {
  override def environment(exec: Exec): IO[DB] = IO {
    Postgres(database, user, password, host, port, poolSize, exec)
  }
}
