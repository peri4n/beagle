package io.beagle.controller

import cats.effect.IO
import io.beagle.components.Controller
import io.beagle.environments.TestEnv
import io.circe.generic.simple.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

class FileUploadControllerSpec extends Specification with Http4sMatchers[IO] with IOMatchers {

  import FileUploadController._

  implicit val responseDecoder: EntityDecoder[IO, FileUploadResponse] = jsonOf[IO, FileUploadResponse]

  "The FileUploadController" should {
    "returns success if the file is correctly uploaded" in {
      val environment = TestEnv.of[FileUploadControllerSpec]
      val response = runAwait(Controller.upload(environment).orNotFound.run(
        Request(method = Method.POST, uri = uri"/upload", body = UrlForm.entityEncoder.toEntity(UrlForm("file" -> ">Some header\nACGT")).body )
      ))

      response must haveStatus(Status.Ok)
      response must haveBody(FileUploadResponse("success"))
    }
  }
}
