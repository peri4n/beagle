package io.beagle.environments.trancaction

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.Env
import io.beagle.components.{Execution, Settings, Transaction}
import io.beagle.components.settings.DatabaseSettings

case class JdbcTransaction(databaseSettings: DatabaseSettings, execution: Execution) extends Transaction {

  lazy val transactor = {
    import databaseSettings._
    import execution._

    Transactor.fromDriverManager[IO](
      driver = driver,
      url = s"$protocol://$host:$port/$database",
      user = username,
      pass = password
    )
  }
}

object JdbcTransaction {

  def instance =
    for {
      databaseSettings <- Settings.database
      execution <- Env.execution
    } yield JdbcTransaction(databaseSettings, execution)
}
