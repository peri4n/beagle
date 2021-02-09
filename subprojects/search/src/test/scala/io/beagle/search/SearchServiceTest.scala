package io.beagle.search

import io.beagle.domain.DatasetId
import io.beagle.search.docs.SequenceDoc
import io.beagle.search.testsupport.SearchSupport
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

class SearchServiceTest extends SearchSupport with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  override def beforeAll = {
    service.createSequenceIndex().unsafeRunSync()
  }

  after {
    service.deleteAll().unsafeRunSync()
  }

  describe("A search service") {
    it("can index sequence entries") {
      val test = for {
        _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
        response <- service.findSequence("ACGT")
      } yield response

      val response = test.unsafeRunSync()
      response.isSuccess should be(true)
      response.result.hits.size should be(1)
    }

    it("can delete sequence entries") {
      val test = for {
        _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
        _ <- service.delete("foo")
        response <- service.findSequence("ACGT")
      } yield response

      val response = test.unsafeRunSync()
      response.isSuccess should be(true)
      response.result.hits.hits shouldBe empty

    }

    it("can find sequence entries") {
      val test = for {
        _ <- service.index(SequenceDoc("foo", 1, "ACGTA"), refresh = true)
        _ <- service.index(SequenceDoc("bar", 2, "GCGTA"), refresh = true)
        _ <- service.index(SequenceDoc("gna", 1, "ACATA"), refresh = true)
        response <- service.findByDataset(DatasetId(1))
      } yield response

      val response = test.unsafeRunSync()
      response.isSuccess should be(true)
      response.result.hits.size should be(2)
    }
  }

}
