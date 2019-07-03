package io.beagle.components

import io.beagle.repository.SequenceSetRepo

trait Repositories {

  def sequenceSet: SequenceSetRepo

}

object Repositories {

  val sequenceSet = Env.repositories map { _.sequenceSet }

}
