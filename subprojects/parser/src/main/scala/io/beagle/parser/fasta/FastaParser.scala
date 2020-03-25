package io.beagle.parser.fasta

import fs2.{Chunk, Pipe, Pure, text}
import cats.implicits._

object FastaParser {

  def parse: Pipe[Pure, Byte, FastaSeq] = s =>
    s.through(text.utf8Decode)
      .repartition(c => Chunk.array(c.split(">")))
      .tail
      .map(FastaParser.toFasta)

  def toFasta(chunk: String): FastaSeq = {
    val (header :: body) = chunk.linesIterator.toList
    FastaSeq(
      header,
      body.map(_.trim).mkString("")
    )
  }
}

case class FastaSeq(header: String, sequence: String)
