package io.beagle.persistence.testsupport

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DB, PostgresConfig}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

trait DockerPostgres extends AnyFunSpec with BeforeAndAfterAll {

  lazy val container = PostgreSQLContainer.Def(
    databaseName = getClass.getSimpleName,
    //    commonJdbcParams = JdbcDatabaseContainer.CommonParams(initScriptPath = Some("schema/create-db.sql"))
  ).start()

  lazy val environment: DB =
    PostgresConfig(
      container.databaseName,
      container.username,
      container.password,
      container.host,
      container.mappedPort(5432),
      Global()
    ).environment().unsafeRunSync()

  lazy val xa = environment.transactor
}
