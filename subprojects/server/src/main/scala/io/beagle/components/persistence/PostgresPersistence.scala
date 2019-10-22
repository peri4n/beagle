package io.beagle.components.persistence

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.Env
import io.beagle.components.{Execution, Settings}

case class PostgresPersistence(databaseSettings: PersistenceSettings, execution: Execution) extends Persistence {

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

object PostgresPersistence {

  def instance =
    for {
      execution <- Env.execution
    } yield PostgresPersistence(null, execution)
}
