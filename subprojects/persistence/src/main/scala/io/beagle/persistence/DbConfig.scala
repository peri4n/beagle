package io.beagle.persistence

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.InMemDatasetRepo
import io.beagle.persistence.repository.project.InMemProjectRepo
import io.beagle.persistence.repository.user.InMemUserRepo

sealed trait DbConfig {

  def environment(exec: Exec): IO[DB]

}

case class DbCredentials(username: String, password: String)

case class PgConfig(host: String = "localhost",
                    port: Int = 5432,
                    database: String = "beagle",
                    credentials: DbCredentials = DbCredentials("beagle", "beagle"),
                    poolSize: Int = 5) extends DbConfig {

  override def environment(exec: Exec) = IO { PgDB(this, exec) }

  def jdbcUrl(): String = s"jdbc:postgresql://$host:$port/$database"

}

case object InMemConfig extends DbConfig {
  override def environment(exec: Exec): IO[DB] = for {
    userRepo <- InMemUserRepo.create()
    projectRepo <- InMemProjectRepo.create()
    datasetRepo <- InMemDatasetRepo.create()
  } yield InMemDB(exec, userRepo, projectRepo, datasetRepo)
}
