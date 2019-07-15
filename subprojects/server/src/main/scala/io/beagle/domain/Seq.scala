package io.beagle.domain

final case class SeqId(id: Long) extends AnyVal

case class Seq(
                id: SeqId,
                identifier: String,
                sequence: String,
              )

case class SeqView(
                  id: SeqId,
                  seq: Seq
                  )
