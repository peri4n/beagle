package io.beagle.web.controller

import cats.effect.IO
import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.testsupport.DbSuite
import io.beagle.web.controller.DatasetController.CreateSequenceSetRequest
import io.circe.generic.auto._
import org.http4s.Status.Ok
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

class DatasetControllerTest extends DbSuite {

  implicit val requestEncoder = jsonEncoderOf[IO, CreateSequenceSetRequest]
  implicit val responseEncoder = jsonEncoderOf[IO, DatasetItem]

  val userService = setup().userService

  val datasetService = setup().datasetService

  val projectService = setup().projectService

  val xa = setup().xa

  test("must return 200 if the dataset was successfully created") {
    // setup
    val (userI, projectI) = (for {
      userItem <- userService.create(User("foo", "1234", "gna@example.com"))
      projectItem <- projectService.create(Project("user", userItem.id))
    } yield (userItem, projectItem)).transact(xa).unsafeRunSync()
    val createRequest = CreateSequenceSetRequest("set1", userI.id, projectI.id)
    val controller = DatasetController(datasetService, xa).route.orNotFound

    // test
    val response = controller
      .run(
        Request(
          method = Method.POST,
          uri = uri"/seqsets",
          body = requestEncoder.toEntity(createRequest).body))
      .unsafeRunSync()

    // verify
    assert(response.status == Ok)
  }

}
