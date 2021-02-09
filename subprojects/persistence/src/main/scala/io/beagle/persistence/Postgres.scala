package io.beagle.persistence

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor.Aux
import io.beagle.exec.Exec
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, Liquibase}

import java.sql.DriverManager

case class Postgres(config: PostgresConfig, execution: Exec) {

  val driverClass: String = "org.postgresql.Driver"

  lazy val transactor: Aux[IO, HikariDataSource] = {
    import execution._

    val hikari = new HikariConfig()
    hikari.setJdbcUrl(config.jdbcUrl())
    hikari.setUsername(config.credentials.username)
    hikari.setPassword(config.credentials.password)
    hikari.setMaximumPoolSize(config.poolSize)
    hikari.setDriverClassName(driverClass)

    HikariTransactor[IO](new HikariDataSource(hikari), execution.context, execution.blocker)
  }

  def initSchema(): IO[Unit] = {
    val con = DriverManager.getConnection(config.jdbcUrl(), config.credentials.username, config.credentials.password)

    val db = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(con))
    val liquibase = new Liquibase("schema/changeset.sql", new ClassLoaderResourceAccessor(), db)
    IO { liquibase.update(new Contexts()) }
  }
}
