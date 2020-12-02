package io.beagle.persistence.testsupport

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{PersistenceEnv, PostgresEnv}
import org.scalatest.funspec.AnyFunSpec

trait PostgresSupport extends AnyFunSpec {

  lazy val container = PostgreSQLContainer.Def(databaseName = getClass.getSimpleName).start()

  lazy val environment: PersistenceEnv = PostgresEnv(
    container.databaseName,
    container.username,
    container.password,
    container.containerIpAddress,
    container.mappedPort(5432),
    Global())

  lazy val xa = environment.transactor
}
