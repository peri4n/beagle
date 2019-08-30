package io.beagle.fasta

import cats.implicits._
import fs2._

object FastaParser {

  def parse: Pipe[Pure, Byte, FastaEntry] = s =>
    s.through(text.utf8Decode)
      .repartition(c => Chunk.array(c.split(">")))
      .tail
      .map(FastaParser.toFasta)

  def toFasta(chunk: String): FastaEntry = {
    val ( header :: body ) = chunk.linesIterator.toList
    FastaEntry(
      header,
      body.map(_.trim).mkString("")
    )
  }
}

case class FastaEntry(header: String, sequence: String)
