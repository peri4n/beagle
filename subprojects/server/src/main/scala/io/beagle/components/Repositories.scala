package io.beagle.components

import io.beagle.repository.dataset.DatasetRepo
import io.beagle.repository.project.ProjectRepo
import io.beagle.repository.seq.SeqRepo
import io.beagle.repository.user.UserRepo

trait Repositories {

  def sequence: SeqRepo

  def dataset: DatasetRepo

  def user: UserRepo

  def project: ProjectRepo

}

object Repositories {

  def sequence = Env.repositories map { _.sequence }

  def dataset = Env.repositories map { _.dataset }

  def user = Env.repositories map { _.user }

  def project = Env.repositories map { _.project }

}
