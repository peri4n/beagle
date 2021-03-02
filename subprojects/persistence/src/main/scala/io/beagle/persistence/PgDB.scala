package io.beagle.persistence

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.DatasetRepo
import io.beagle.persistence.repository.project.ProjectRepo
import io.beagle.persistence.repository.user.PgUserRepo
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}
import liquibase.{Contexts, Liquibase}
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import java.sql.DriverManager

case class PgDB(config: PgConfig, execution: Exec) extends DB {

  import execution._

  val driverClass: String = "org.postgresql.Driver"

  lazy val hikari = new HikariConfig()
  hikari.setJdbcUrl(config.jdbcUrl())
  hikari.setUsername(config.credentials.username)
  hikari.setPassword(config.credentials.password)
  hikari.setMaximumPoolSize(config.poolSize)
  hikari.setDriverClassName(driverClass)

  lazy val xa = HikariTransactor[IO](new HikariDataSource(hikari), execution.context, execution.blocker)

  lazy val userService: UserService = UserService(PgUserRepo)

  lazy val projectService: ProjectService = ProjectService(userService, ProjectRepo)

  lazy val datasetService: DatasetService = DatasetService(DatasetRepo)

  def initSchema(): IO[Unit] = {
    val con = DriverManager.getConnection(config.jdbcUrl(), config.credentials.username, config.credentials.password)

    val db = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(con))
    val liquibase = new Liquibase("schema/changeset.sql", new ClassLoaderResourceAccessor(), db)
    IO { liquibase.update(new Contexts()) }
  }
}
