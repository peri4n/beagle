package io.beagle.components

import io.beagle.repository.seqset.SeqSetRepo

trait Repositories {

  def sequenceSet: SeqSetRepo

}

object Repositories {

  val sequenceSet = Env.repositories map { _.sequenceSet }

}
