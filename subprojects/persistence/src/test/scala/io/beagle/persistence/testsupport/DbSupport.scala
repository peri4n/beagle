package io.beagle.persistence.testsupport

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DB, PostgresConfig}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

trait DbSupport extends AnyFunSpec with BeforeAndAfterAll {

  lazy val container = PostgreSQLContainer.Def(
    databaseName = getClass.getSimpleName,
    //    commonJdbcParams = JdbcDatabaseContainer.CommonParams(initScriptPath = Some("schema/create-db.sql"))
  ).start()


  private val exec: Global = Global()

  lazy val environment: DB = (System.getProperty("run.mode") match {
    case "dev" =>
      PostgresConfig(
        "beagle",
        "beagle",
        "beagle",
        "localhost",
        5432,
        1,
        exec)
    case _ =>
      PostgresConfig(
        container.databaseName,
        container.username,
        container.password,
        container.host,
        container.mappedPort(5432),
        1,
        exec)
  }).environment().unsafeRunSync()

  lazy val xa = environment.transactor

  override def beforeAll(): Unit = {
    if (System.getProperty("run.mode") == "prod") {
      container.start()
    }
    environment.initSchema().unsafeRunSync()
  }
}
