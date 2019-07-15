package io.beagle.fasta

import cats.implicits._
import cats.effect.{ContextShift, IO}
import scala.concurrent.ExecutionContext.Implicits.global
import fs2._

object FastaParser {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  def toFasta(chunk: String): FastaEntry = {
    val ( header :: body ) = chunk.lines.toList
    FastaEntry(
      header,
      body.map(_.trim).mkString("")
    )
  }

  def parse: Pipe[IO, Byte, FastaEntry] = s =>
    s.through(text.utf8Decode)
      .repartition(c => Chunk.array(c.split(">")))
      .tail
      .map(FastaParser.toFasta)
}

case class FastaEntry(header: String, sequence: String)
