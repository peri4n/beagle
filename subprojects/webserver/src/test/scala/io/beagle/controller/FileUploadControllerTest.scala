package io.beagle.controller

import cats.effect.IO
import io.beagle.exec.testsupport.ExecutionSupport
import io.beagle.search.Search
import io.beagle.search.testsupport.SearchSupport
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FileUploadControllerTest extends AnyFunSpec with Matchers with ResponseMatchers with SearchSupport with ExecutionSupport with OptionValues {

  import FileUploadController._

  implicit val responseDecoder: EntityDecoder[IO, FileUploadResponse] = jsonOf[IO, FileUploadResponse]

  describe("The FileUploadController") {
    it("returns success if the file is correctly uploaded") {
      val controller = FileUploadController(execution, service).route.orNotFound
      val response = controller
        .run(Request(
          method = Method.POST,
          uri = uri"/upload",
          body = UrlForm.entityEncoder.toEntity(UrlForm("file" -> ">Some header\nACGT")).body))
        .unsafeRunSync()

      response should have {
        status(Status.Ok)
      }
    }
  }
}
