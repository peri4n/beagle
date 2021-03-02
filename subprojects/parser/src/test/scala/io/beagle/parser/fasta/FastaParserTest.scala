package io.beagle.parser.fasta

import cats.effect._
import fs2._
import munit.CatsEffectSuite

import java.nio.file.Paths
import scala.concurrent.ExecutionContext

class FastaParserTest extends CatsEffectSuite {

  val blocker = Blocker.liftExecutionContext(ExecutionContext.global)

  test("can parse an empty file") {
    val entries = Stream.empty
      .through(FastaParser.parse)
      .compile
      .toList

    assertIO(entries, returns = List())
  }

  test("can handle empty entries within a FASTA files") {
    val path = Paths.get(getClass.getResource("invalid.fasta").toURI)

    val entries = io.file
      .readAll[IO](path, blocker, 4096)
      .through(FastaParser.parse)
      .compile
      .toList

    assertIO(entries, returns = List(
      FastaSeq("Test1", ""),
      FastaSeq("Test2", "")))
  }

  test("can parse a FASTA file with a incomplete last line") {
    val path = Paths.get(getClass.getResource("easy_split.fasta").toURI)

    val entries = io.file
      .readAll[IO](path, blocker, 4096)
      .through(FastaParser.parse)
      .compile
      .toList

    assertIO(entries, returns = List(
      FastaSeq("Test1", "AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGACTGATAT"),
      FastaSeq("Test2", "CCCGCACCTGACAGTGCGGGCTTTTTT")
    ))
  }

  test("can parse a usual FASTA file") {
    val path = Paths.get(getClass.getResource("easy.fasta").toURI)

    val entries = io.file
      .readAll[IO](path, blocker, 4096)
      .through(FastaParser.parse)
      .compile
      .toList

    assertIO(entries, List(
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
