package io.beagle.controller

import cats.effect.IO
import io.beagle.controller.DatasetController.CreateSequenceSetRequest
import io.beagle.domain.{DatasetId, DatasetItem}
import io.beagle.environments.Test
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.http4s.{Request, _}
import org.http4s.circe.CirceEntityDecoder._
import org.specs2.mutable.Specification

class DatasetControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  implicit val requestEncoder = jsonEncoderOf[IO, CreateSequenceSetRequest]

  "DatasetController" should {
    "must return 200 if the user was successfully created" in {
      val environment = Test.of[DatasetControllerSpec]
      val createRequest1 = CreateSequenceSetRequest("set1", "DNA")
      val createRequest2 = CreateSequenceSetRequest("set2", "DNA")
      val controller = new DatasetController(environment.repositories.sequenceSet).route.orNotFound

      val test = for {
        response1 <- controller.run(
          Request(
            method = Method.POST,
            uri = uri"/seqsets",
            body = requestEncoder.toEntity(createRequest1).body))
        response2 <- controller.run(
          Request(
            method = Method.POST,
            uri = uri"/seqsets",
            body = requestEncoder.toEntity(createRequest2).body))
      } yield (response1, response2)

      val (response1, response2) = runAwait(test)

      response1 must haveStatus(Status.Ok)
      response1 must haveBody { view: DatasetItem =>
        view.id must beEqualTo(DatasetId(1))
        view.set.name must beEqualTo("set1")
      }

      response2 must haveStatus(Status.Ok)
      response2 must haveBody { view: DatasetItem =>
        view.id must beEqualTo(DatasetId(2))
        view.set.name must beEqualTo("set2")
      }
    }
  }

}