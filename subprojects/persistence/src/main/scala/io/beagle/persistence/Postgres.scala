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

case class Postgres(database: String,
                    user: String,
                    password: String,
                    host: String = "localhost",
                    port: Int = 5432,
                    poolSize: Int = 5,
                    execution: Exec) extends DB {

  val driverClass: String = "org.postgresql.Driver"

  lazy val transactor: Aux[IO, HikariDataSource] = {
    import execution._

    val config = new HikariConfig()
    config.setJdbcUrl(s"jdbc:postgresql://$host:$port/$database")
    config.setUsername(user)
    config.setPassword(password)
    config.setMaximumPoolSize(poolSize)
    config.setDriverClassName(driverClass)

    HikariTransactor[IO](new HikariDataSource(config), execution.context, execution.blocker)
  }

  override def initSchema(): IO[Unit] = {
    val con = DriverManager.getConnection(s"jdbc:postgresql://$host:$port/$database", user, password)

    val db = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(con))
    val liquibase = new Liquibase("schema/changeset.sql", new ClassLoaderResourceAccessor(), db)
    IO { liquibase.update(new Contexts()) }
  }
}
