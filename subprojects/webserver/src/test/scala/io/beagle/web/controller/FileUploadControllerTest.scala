package io.beagle.web.controller

import cats.effect.IO
import io.beagle.exec.testsupport.ExecutionSupport
import io.beagle.search.testsupport.SearchSuite
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

class FileUploadControllerTest extends SearchSuite with ExecutionSupport {

  import FileUploadController._

  val service = setup().searchService

  implicit val responseDecoder: EntityDecoder[IO, FileUploadResponse] = jsonOf[IO, FileUploadResponse]

  test("returns success if the file is correctly uploaded") {
    val controller = FileUploadController(execution, service).route.orNotFound

    val test = controller
      .run(Request(
        method = Method.POST,
        uri = uri"/upload",
        body = UrlForm.entityEncoder.toEntity(UrlForm("file" -> ">Some header\nACGT")).body))

    val responseCode = test.map { _.status }

    assertIO(responseCode, Status.Ok)
  }
}
