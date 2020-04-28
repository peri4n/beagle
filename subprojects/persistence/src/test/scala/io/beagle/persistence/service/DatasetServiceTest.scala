package io.beagle.persistence.service

import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.service.testsupport.PersistenceSupport
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DatasetServiceTest extends PersistenceSupport with ScalaCheckPropertyChecks with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  override def beforeAll = {
    (for {
      _ <- UserService.createTable()
      _ <- ProjectService.createTable()
      _ <- DatasetService.createTable()
    } yield ()).transact(xa).unsafeRunSync()
  }

  after {
    (for {
      _ <- DatasetService.deleteAll()
      _ <- ProjectService.deleteAll()
      _ <- UserService.deleteAll()
    } yield ()).transact(xa).unsafeRunSync()
  }

  describe("A dataset service") {
    it("can create a dataset") {
      val test = (for {
        userItem <- UserService.create(User("foo", "1234", "gna@example.com"))
        projectItem <- ProjectService.create(Project("user", userItem.id))
        datasetItem <- DatasetService.create(Dataset("dataset", userItem.id, projectItem.id))
      } yield datasetItem).transact(xa)

      test.unsafeRunSync().dataset.name should be("dataset")
    }
  }

}
