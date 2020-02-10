package io.beagle.controller

import cats.effect.IO
import io.beagle.Env.TestEnv
import io.beagle.components.Web
import io.beagle.controller.DatasetController.CreateSequenceSetRequest
import io.beagle.domain.{DatasetId, DatasetItem, ProjectId}
import io.circe.generic.simple.auto._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.http4s.{Request, _}
import org.http4s.circe.CirceEntityDecoder._
import org.specs2.mutable.Specification

class DatasetControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  implicit val requestEncoder = jsonEncoderOf[IO, CreateSequenceSetRequest]

  "The DatasetController" should {

    "must return 200 if the user was successfully created" in {
      val createRequest1 = CreateSequenceSetRequest("set1", ProjectId(1))
      val createRequest2 = CreateSequenceSetRequest("set2", ProjectId(2))

      val test = for {
        // setup
        env <- TestEnv.of[DatasetControllerSpec]
        controller = Web.dataset(env).orNotFound

        // test
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
        view.dataset.name must beEqualTo("set1")
      }

      response2 must haveStatus(Status.Ok)
      response2 must haveBody { view: DatasetItem =>
        view.id must beEqualTo(DatasetId(2))
        view.dataset.name must beEqualTo("set2")
      }
    }
  }

}
