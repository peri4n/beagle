package io.beagle.components

import io.beagle.repository.dataset.DatasetRepo

trait Repositories {

  def sequenceSet: DatasetRepo

}

object Repositories {

  def sequenceSet = Env.repositories map { _.sequenceSet }

}
