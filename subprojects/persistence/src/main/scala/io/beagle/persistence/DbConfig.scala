package io.beagle.persistence

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.InMemDatasetRepo
import io.beagle.persistence.repository.project.InMemProjectRepo
import io.beagle.persistence.repository.user.InMemUserRepo

sealed trait DbConfig {

  def environment(): IO[DB]

}

case class PostgresConfig(database: String,
                          user: String,
                          password: String,
                          host: String = "localhost",
                          port: Int = 5432,
                          exec: Exec) extends DbConfig {
  override def environment(): IO[DB] = IO {
    Postgres(database, user, password, host, port, exec)
  }
}

case class InMemConfig(exec: Exec) extends DbConfig {
  override def environment(): IO[DB] = for {
    userRepo <- InMemUserRepo.create()
    projectRepo <- InMemProjectRepo.create()
    datasetRepo <- InMemDatasetRepo.create()
  } yield InMemDB(exec, userRepo, projectRepo, datasetRepo)
}
