package io.beagle.persistence

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.repository.dataset.InMemDatasetRepo
import io.beagle.persistence.repository.project.InMemProjectRepo
import io.beagle.persistence.repository.user.InMemUserRepo

sealed trait PersistenceSettings {

  def environment(): IO[PersistenceEnv]

}

case class Postgres(database: String,
                    user: String,
                    password: String,
                    host: String = "localhost",
                    port: Int = 5432,
                    exec: Exec) extends PersistenceSettings {
  override def environment(): IO[PersistenceEnv] = IO { PostgresEnv(database, user, password, host, port, exec) }
}

case class InMemDB(exec: Exec) extends PersistenceSettings {
  override def environment(): IO[PersistenceEnv] = for {
    userRepo <- InMemUserRepo.create()
    projectRepo <- InMemProjectRepo.create()
    datasetRepo <- InMemDatasetRepo.create()
  } yield InMemEnv(exec, userRepo, projectRepo, datasetRepo)
}
