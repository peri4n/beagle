package io.beagle.fasta

import java.nio.file.Paths
import java.util.concurrent.Executors

import cats.effect._
import fs2._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class FastaParserSpec extends Specification {

  val blocker = Blocker.liftExecutionContext(
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2)))

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  "A Fasta parser" should {
    "can parse an empty file" in {
      val entries = Stream.empty
        .through(FastaParser.parse)
        .compile
        .toList

      entries must beEmpty
    }
    "can handle empty entries within a FASTA files" in {
      val path = Paths.get(getClass.getResource("invalid.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries must haveSize(2)
      entries must beEqualTo(List(
        FastaEntry("Test1", ""),
        FastaEntry("Test2", "")
      ))
    }
    "can parse a FASTA file with a incomplete last line" in {
      val path = Paths.get(getClass.getResource("easy_split.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries must haveSize(2)
      entries must beEqualTo(List(
        FastaEntry("Test1", "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGACTGATAT"),
        FastaEntry("Test2", "CCCGCACCTGACAGTGCGGGCTTTTTT")
      ))
    }
    "can parse a usual FASTA file" in {
      val path = Paths.get(getClass.getResource("easy.fasta").toURI)

      val entries = io.file
        .readAll[IO](path, blocker, 4096)
        .through(FastaParser.parse)
        .compile
        .toList
        .unsafeRunSync()

      entries must haveSize(2)
      entries must beEqualTo(List(
        FastaEntry("Test1",
          """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
            |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
            |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
            |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.replace("\n", "")),
        FastaEntry("Test2 description",
          """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
            |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
            |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
            |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.replace("\n", ""))
      ))
    }

  }
}
