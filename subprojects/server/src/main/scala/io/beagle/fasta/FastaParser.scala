package io.beagle.fasta

import cats.implicits._
import fs2._

object FastaParser {

  def toFasta(chunk: String): FastaEntry = {
    val ( header :: body ) = chunk.lines.toList
    FastaEntry(
      header,
      body.map(_.trim).mkString("")
    )
  }

  def parse: Pipe[Pure, Byte, FastaEntry] = s =>
    s.through(text.utf8Decode)
      .repartition(c => Chunk.array(c.split(">")))
      .tail
      .map(FastaParser.toFasta)
}

case class FastaEntry(header: String, sequence: String)
