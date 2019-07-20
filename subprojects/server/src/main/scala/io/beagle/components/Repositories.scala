package io.beagle.components

import io.beagle.repository.seqset.SeqSetRepo

trait Repositories {

  def sequenceSet: SeqSetRepo

}

object Repositories {

  def sequenceSet = Env.repositories map { _.sequenceSet }

}
