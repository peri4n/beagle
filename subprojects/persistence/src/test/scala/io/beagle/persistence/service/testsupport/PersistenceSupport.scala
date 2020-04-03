package io.beagle.persistence.service.testsupport

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import io.beagle.exec.Execution
import io.beagle.persistence.Postgres
import org.scalatest.funspec.AnyFunSpec

trait PersistenceSupport extends AnyFunSpec with ForAllTestContainer {

  val container = {
    val pg = PostgreSQLContainer(databaseName = getClass.getSimpleName)
    pg.start()
    pg
  }

  lazy val persistence = Postgres(container.databaseName,
    container.username,
    container.password,
    container.containerIpAddress,
    container.mappedPort(Postgres.port),
    Execution.Global)

  lazy val xa = persistence.transactor
}
