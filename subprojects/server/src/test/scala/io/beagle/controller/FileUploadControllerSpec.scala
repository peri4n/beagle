package io.beagle.controller

import cats.effect.IO
import io.beagle.Env.TestEnv
import io.beagle.components.Web
import io.beagle.testsupport.ResponseMatchers
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.specs2.mutable.Specification

class FileUploadControllerSpec extends Specification with ResponseMatchers {

  import FileUploadController._

  implicit val responseDecoder: EntityDecoder[IO, FileUploadResponse] = jsonOf[IO, FileUploadResponse]

  "The FileUploadController" should {
    "returns success if the file is correctly uploaded" in {
      val test = for {
        // setup
        environment <- TestEnv.of[FileUploadControllerSpec]
        controller = Web.upload(environment).orNotFound

        // test
        response <- controller.run(
          Request(method = Method.POST, uri = uri"/upload", body = UrlForm.entityEncoder.toEntity(UrlForm("file" -> ">Some header\nACGT")).body )
        )
      } yield response
      val response = runAwait(test)

      response must haveStatus(Status.Ok)
      response must haveBody(FileUploadResponse("success"))
    }
  }
}
