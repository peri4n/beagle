package io.beagle.persistence.testsupport

import doobie.implicits._
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{DB, DbCredentials, InMemConfig, PgConfig}
import munit.CatsEffectSuite
import org.testcontainers.containers.PostgreSQLContainer

trait DbSuite extends CatsEffectSuite {

  override def munitFixtures = List(setup)

  val setup = new Fixture[DB]("database") {
    val db = (System.getProperty("run.mode") match {
      case "dev" => InMemConfig
      case _ =>
        val container: PostgreSQLContainer[_] = new PostgreSQLContainer("postgres:13.2")
          .withDatabaseName(getClass.getSimpleName)
        container.start()

        PgConfig(container.getHost,
          container.getMappedPort(5432),
          container.getDatabaseName,
          DbCredentials(container.getUsername, container.getPassword),
          1)
    }).environment(Global).unsafeRunSync()

    def apply() = db

    override def beforeAll(): Unit = db.initSchema().unsafeRunSync()

    override def afterEach(context: AfterEach): Unit = {
      val deleteAll = for {
        _ <- db.userService.deleteAll()
        //        _ <- setup.projectService.deleteAll() to be implemented
        //        _ <- setup.datasetService.deleteAll()
      } yield ()

      deleteAll.transact(db.xa)
    }

  }
}
