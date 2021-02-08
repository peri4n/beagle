package io.beagle.web.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.service.{DatasetService, ProjectService, UserService}
import io.beagle.persistence.testsupport.DbSupport
import io.beagle.testsupport.ResponseMatchers
import io.beagle.web.controller.DatasetController.CreateSequenceSetRequest
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, OptionValues}

class DatasetControllerTest extends AnyFunSpec with Matchers with ResponseMatchers with DbSupport with OptionValues with BeforeAndAfter with BeforeAndAfterAll {

  implicit val requestEncoder = jsonEncoderOf[IO, CreateSequenceSetRequest]
  implicit val responseEncoder = jsonEncoderOf[IO, DatasetItem]

  override def beforeAll = {
    environment.initSchema().unsafeRunSync()
  }

  after {
    (for {
      _ <- DatasetService.deleteAll()
      _ <- ProjectService.deleteAll()
      _ <- UserService.deleteAll()
    } yield ()).transact(xa).unsafeRunSync()
  }

  describe("The DatasetController") {

    it("must return 200 if the dataset was successfully created") {
      // setup
      val (userI, projectI) = (for {
        userItem <- UserService.create(User("foo", "1234", "gna@example.com"))
        projectItem <- ProjectService.create(Project("user", userItem.id))
      } yield (userItem, projectItem)).transact(xa).unsafeRunSync()
      val createRequest = CreateSequenceSetRequest("set1", userI.id, projectI.id)
      val controller = DatasetController(xa).route.orNotFound

      // test
      val response = controller
        .run(
          Request(
            method = Method.POST,
            uri = uri"/seqsets",
            body = requestEncoder.toEntity(createRequest).body))
        .unsafeRunSync()

      // verify
      response should have {
        status(Status.Ok)
      }
    }
  }

}
