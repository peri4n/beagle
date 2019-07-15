package io.beagle.fasta

import java.nio.file.Paths
import java.util.concurrent.Executors

import cats.effect._
import cats.implicits._
import fs2._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class FastaParserSpec extends Specification {
  "iearnstoearst" should {
    "insartin" in {
      val blockingExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

      implicit val cs: ContextShift[IO] = IO.contextShift(global)

      val entries = io.file
        .readAll[IO](Paths.get("/home/fbull/test.fasta"), blockingExecutionContext, 4096)
        .through(text.utf8Decode)
        .repartition(c => Chunk.array(c.split(">")))
        .tail
        .map(FastaParser.toFasta)
        .compile
        .toList
        .unsafeRunSync()

      entries.foreach { println }

      true must beEqualTo(true)
    }

  }
}
