package io.beagle.parser.fasta

import cats.effect.IO
import cats.implicits._
import fs2.{Chunk, Pipe, text}

object FastaParser {

  def parse: Pipe[IO, Byte, FastaSeq] = s =>
    s.through(text.utf8Decode)
      .repartition(c => Chunk.array(c.split(">")))
      .tail
      .map(FastaParser.toFasta)

  def toFasta(chunk: String): FastaSeq = {
    val iterator = chunk.linesIterator
    val (header, body) = (iterator.next(), iterator)
    FastaSeq(
      header,
      body.mkString("")
    )
  }
}

case class FastaSeq(header: String, sequence: String)
