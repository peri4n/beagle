package io.beagle.components

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

  def sequence = Env.repositories map { _.sequence }

  def dataset = Env.repositories map { _.dataset }

  def user = Env.repositories map { _.user }

  def project = Env.repositories map { _.project }

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
