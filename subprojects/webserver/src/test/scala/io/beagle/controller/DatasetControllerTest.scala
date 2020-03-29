package io.beagle.controller

import cats.effect.IO
import io.beagle.controller.DatasetController.CreateSequenceSetRequest
import io.beagle.domain._
import io.beagle.persistence.service.testsupport.PersistenceSupport
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DatasetControllerTest extends AnyFunSpec with Matchers with ResponseMatchers with PersistenceSupport with OptionValues {

  implicit val requestEncoder = jsonEncoderOf[IO, CreateSequenceSetRequest]
  implicit val responseEncoder = jsonEncoderOf[IO, DatasetItem]

  describe("The DatasetController") {

    it("must return 200 if the dataset was successfully created") {
      // setup
      val createRequest = CreateSequenceSetRequest("set1", UserId(1), ProjectId(1))
      val controller = DatasetController(persistence).route.orNotFound

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
        body(responseEncoder.toEntity(DatasetItem(DatasetId(1), Dataset("ds", UserId(1), ProjectId(1)))).body)
      }
    }
  }

}
