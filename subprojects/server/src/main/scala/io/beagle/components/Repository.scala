package io.beagle.components

import cats.data.Reader
import io.beagle.Env
import io.beagle.repository.dataset.DatasetRepo
import io.beagle.repository.project.ProjectRepo
import io.beagle.repository.seq.SeqRepo
import io.beagle.repository.user.UserRepo

sealed trait Repository {

  def sequence: SeqRepo

  def dataset: DatasetRepo

  def user: UserRepo

  def project: ProjectRepo

}

object Repository {

  private val repository = Reader[Env, Repository](_.repositories)

  def sequence = repository map { _.sequence }

  def dataset = repository map { _.dataset }

  def user = repository map { _.user }

  def project = repository map { _.project }

  case class DevRepository() extends Repository {
    lazy val sequence: SeqRepo = SeqRepo.inMemory

    lazy val dataset: DatasetRepo = DatasetRepo.inMemory

    lazy val user: UserRepo = UserRepo.inMemory

    lazy val project: ProjectRepo = ProjectRepo.inMemory
  }

  case class ProdRepository() extends Repository {
    lazy val sequence: SeqRepo = ???

    lazy val dataset: DatasetRepo = DatasetRepo.inDB

    lazy val user: UserRepo = UserRepo.inDB

    lazy val project: ProjectRepo = ProjectRepo.inDB
  }

}
