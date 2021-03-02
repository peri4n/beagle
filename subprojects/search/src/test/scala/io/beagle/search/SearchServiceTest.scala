package io.beagle.search

import io.beagle.domain.DatasetId
import io.beagle.search.docs.SequenceDoc
import io.beagle.search.testsupport.SearchSuite

class SearchServiceTest extends SearchSuite {

  val service = setup().searchService

  test("can index sequence entries") {
    val test = for {
      _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
      response <- service.findSequence("ACGT")
    } yield response

    val numberOfHits = test.map(_.result.hits.size)

    assertIO(numberOfHits, returns = 1L)
  }

  test("can delete sequence entries") {
    val test = for {
      _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
      _ <- service.delete("foo")
      response <- service.findSequence("ACGT")
    } yield response

    val numberOfHits = test.map(_.result.hits.size)

    assertIO(numberOfHits, returns = 0L)
  }

  test("can find sequence entries") {
    val test = for {
      _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
      _ <- service.index(SequenceDoc("bar", 2, "GCGTA"), refresh = true)
      _ <- service.index(SequenceDoc("gna", 1, "ACATA"), refresh = true)
      response <- service.findByDataset(DatasetId(1))
    } yield response

    val numberOfHits = test.map(_.result.hits.size)

    assertIO(numberOfHits, returns = 2L)
  }
}
