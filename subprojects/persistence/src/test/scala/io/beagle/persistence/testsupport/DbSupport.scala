package io.beagle.persistence.testsupport

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DbCredentials, Postgres, PostgresConfig}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

trait DbSupport extends AnyFunSpec with BeforeAndAfterAll {

  lazy val container = PostgreSQLContainer.Def(
    databaseName = getClass.getSimpleName,
    //    commonJdbcParams = JdbcDatabaseContainer.CommonParams(initScriptPath = Some("schema/create-db.sql"))
  ).start()

  lazy val environment: Postgres = (System.getProperty("run.mode") match {
    case "dev" =>
      PostgresConfig("localhost", 5432, "beagle", DbCredentials("beagle", "beagle"), 1)
    case _ =>
      PostgresConfig(container.host,
        container.mappedPort(5432),
        container.databaseName,
        DbCredentials(container.username, container.password),
        1)
  }).environment(Global())

  lazy val xa = environment.transactor

  override def beforeAll(): Unit = {
    if (System.getProperty("run.mode") == "prod") {
      container.start()
    }
    environment.initSchema().unsafeRunSync()
  }
}
