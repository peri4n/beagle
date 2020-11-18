package io.beagle.parser.fasta

import java.nio.file.Paths
import java.util.concurrent.Executors

import cats.effect._
import fs2._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class FastaParserTest extends AnyFunSpec with Matchers {

  val blocker = Blocker.liftExecutionContext(
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2)))

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  describe("A Fasta parser") {
    it("can parse an empty file") {
      val entries = Stream.empty
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries shouldBe empty
    }

    it("can handle empty entries within a FASTA files") {
      val path = Paths.get(getClass.getResource("invalid.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries should have size (2)
      entries should be(List(
        FastaSeq("Test1", ""),
        FastaSeq("Test2", "")
      ))
    }

    it("can parse a FASTA file with a incomplete last line") {
      val path = Paths.get(getClass.getResource("easy_split.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries should have size (2)
      entries should be(List(
        FastaSeq("Test1", "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGACTGATAT"),
        FastaSeq("Test2", "CCCGCACCTGACAGTGCGGGCTTTTTT")
      ))
    }

    it("can parse a usual FASTA file") {
      val path = Paths.get(getClass.getResource("easy.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries should have size (2)
      entries should be(List(
        FastaSeq("Test1",
          """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
            |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
            |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
            |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.replace("\n", "")),
        FastaSeq("Test2 description",
          """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
            |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
            |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
            |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.replace("\n", ""))
      ))
    }

  }
}
