package io.beagle.controller

import cats.effect.IO
import io.beagle.controller.SequenceSetController.CreateSequenceSetRequest
import io.beagle.domain.SequenceSet
import io.beagle.environments.Test
import org.http4s.{Request, _}
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class SequenceSetControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  import SequenceSet._

  implicit val requestEncoder: EntityEncoder[IO, CreateSequenceSetRequest] = jsonEncoderOf[IO, CreateSequenceSetRequest]

  "arsntoerst" should {
    "iansrte" in {
      val environment = Test.of[SequenceSetControllerSpec]
      val createRequest1 = CreateSequenceSetRequest("set1", "DNA")
      val createRequest2 = CreateSequenceSetRequest("set2", "DNA")
      val controller = new SequenceSetController(environment.repositories.sequenceSet).route.orNotFound

      val test = for {
        response1 <- controller.run(Request(method = Method.POST, uri = uri"/sequences", body = requestEncoder.toEntity(createRequest1).body))
        response2 <- controller.run(Request(method = Method.POST, uri = uri"/sequences", body = requestEncoder.toEntity(createRequest2).body))
      } yield (response1, response2)

      val (response1, response2) = runAwait(test)

      response1 must haveStatus(Status.Ok)
      response1 must haveBody { set: SequenceSet =>
        set.id must beEqualTo(1)
        set.name must beEqualTo("set1")
      }

      response2 must haveStatus(Status.Ok)
      response2 must haveBody { set: SequenceSet =>
        set.id must beEqualTo(2)
        set.name must beEqualTo("set2")
      }
    }
  }

}
