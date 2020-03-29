package io.beagle.persistence.service.testsupport

import cats.effect.IO
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import io.beagle.exec.Execution
import io.beagle.persistence.Persistence
import io.beagle.persistence.Persistence.{Postgres, TestPersistence}
import org.scalatest.funspec.AnyFunSpec

trait PersistenceSupport extends AnyFunSpec with ForAllTestContainer {

  val container = {
    val pg = PostgreSQLContainer(databaseName = getClass.getSimpleName)
    pg.start()
    pg
  }

  lazy val persistence = {
    val execution = Execution.Global
    implicit val pool = execution.threadPool

    val config = new HikariConfig()
    config.setJdbcUrl(container.jdbcUrl)
    config.setUsername(container.username)
    config.setPassword(container.password)
    config.setDriverClassName(container.driverClassName)
    config.setMaximumPoolSize(5)

    val xa = HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)

    TestPersistence(xa, execution)
  }

  lazy val xa = persistence.transactor
}
