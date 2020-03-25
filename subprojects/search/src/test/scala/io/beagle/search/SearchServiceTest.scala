package io.beagle.search

import io.beagle.exec.Execution
import io.beagle.search.docs.FastaDoc
import io.beagle.search.testsupport.ForAllElasticSearchContainer
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.should.Matchers

class SearchServiceTest extends ForAllElasticSearchContainer with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  val service = SearchService(Execution.global, "fasta", client)

  override def beforeAll = {
    service.createSequenceIndex().unsafeRunSync()
  }

  after {
    service.deleteAll().unsafeRunSync()
  }

  describe("A search service") {
    it("can index Fasta entries") {
      val test = for {
        _ <- service.index(FastaDoc("foo", 1, "ACGTA"), refresh = true)
        response <- service.find("ACGT")
      } yield response

      val response = test.unsafeRunSync()
      response.isSuccess should be(true)
    }

    it("can delete Fasta entries") {
      val test = for {
        _ <- service.index(FastaDoc("foo", 1, "ACGTA"), refresh = true)
        _ <- service.delete("foo")
        response <- service.find("ACGT")
      } yield response

      val response = test.unsafeRunSync()
      response.isSuccess should be(true)
      response.result.hits.hits shouldBe empty

    }
  }

}
