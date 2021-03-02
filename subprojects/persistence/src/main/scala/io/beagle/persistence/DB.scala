package io.beagle.persistence

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.beagle.exec.Exec
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}

trait DB {

  def initSchema(): IO[Unit]

  def execution: Exec

  def userService: UserService

  def projectService: ProjectService

  def datasetService: DatasetService

  def xa: Transactor[IO]

}




