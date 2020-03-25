package io.beagle.persistence.service.testsupport

import cats.effect.IO
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import io.beagle.exec.Execution
import org.scalatest.funspec.AnyFunSpec

trait ForAllPostgresContainer extends AnyFunSpec with ForAllTestContainer {

  val container = {
    val pg = PostgreSQLContainer(databaseName = getClass.getSimpleName)
    pg.start()
    pg
  }

  implicit lazy val xa = {
    val execution = Execution.global
    implicit val pool = execution.threadPool

    val config = new HikariConfig()
    config.setJdbcUrl(container.jdbcUrl)
    config.setUsername(container.username)
    config.setPassword(container.password)
    config.setDriverClassName(container.driverClassName)
    config.setMaximumPoolSize(5)

    HikariTransactor.apply[IO](new HikariDataSource(config), execution.context, execution.blocker)
  }

}
