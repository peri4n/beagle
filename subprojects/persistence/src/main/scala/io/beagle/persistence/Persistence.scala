package io.beagle.persistence

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import io.beagle.exec.Execution

sealed trait Persistence {

  def transactor: Transactor.Aux[IO, HikariDataSource]

}

object Persistence {

  def of(name: String, execution: Execution) = Postgres(name, "fbull", "", execution = execution)

  case class Postgres(database: String,
                      username: String,
                      password: String,
                      host: String = "localhost",
                      port: Int = 5432,
                      protocol: String = "jdbc:postgresql",
                      driver: String = "org.postgresql.Driver",
                      execution: Execution) extends Persistence {

    lazy val transactor = {
      implicit val pool = execution.threadPool

      val config = new HikariConfig()
      config.setJdbcUrl(s"jdbc:postgresql://$host:$port/postgres")
      config.setUsername(username)
      config.setPassword(password)
      config.setMaximumPoolSize(5)

      HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)
    }

  }

}

