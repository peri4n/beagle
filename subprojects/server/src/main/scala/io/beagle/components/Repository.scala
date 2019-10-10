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
    val sequence: SeqRepo = SeqRepo.inMemory

    val dataset: DatasetRepo = DatasetRepo.inMemory

    val user: UserRepo = UserRepo.inMemory

    val project: ProjectRepo = ProjectRepo.inMemory
  }

  case class ProdRepository() extends Repository {
    val sequence: SeqRepo = ???

    val dataset: DatasetRepo = DatasetRepo.inDB

    val user: UserRepo = UserRepo.inDB

    val project: ProjectRepo = ProjectRepo.inDB
  }

}
