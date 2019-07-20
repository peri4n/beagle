package io.beagle.domain

final case class SeqId(id: Long) extends AnyVal

case class Seq(identifier: String, sequence: String)

case class SeqItem(id: SeqId, seq: Seq)
