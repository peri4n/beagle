package io.beagle.persistence.testsupport

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.beagle.exec.Exec
import io.beagle.exec.Exec.Global
import io.beagle.persistence.{InMemDB, PersistenceEnv, Postgres, PostgresEnv}
import org.scalatest.funspec.AnyFunSpec

trait PostgresSupport extends AnyFunSpec {

  lazy val container = PostgreSQLContainer.Def(databaseName = getClass.getSimpleName).start()

  lazy val environment: PersistenceEnv = (sys.props("run.mode") match {
    case "prod" => Postgres(
      container.databaseName,
      container.username,
      container.password,
      container.containerIpAddress,
      container.mappedPort(5432),
      Global())
    case _ => InMemDB(Exec.Global())
  }).environment().unsafeRunSync()

  lazy val xa = environment.transactor
}
