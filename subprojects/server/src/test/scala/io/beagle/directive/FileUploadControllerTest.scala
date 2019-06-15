package io.beagle.directive

import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.beagle.TestEnv
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration._

class FileUploadControllerTest extends FunSuite with Matchers with ScalatestRouteTest with FailFastCirceSupport {

  import io.circe.generic.auto._

  implicit val timeout = RouteTestTimeout(5.seconds dilated)

  val route = FileUploadController.route.run(TestEnv(system))

  test("foo") {
    Post("/upload", FileUploadActor.FileUploadRequest(">test1\nACTG")) ~> route ~> check {
      responseAs[FileUploadActor.FileUploadResponse] shouldEqual FileUploadActor.FileUploadResponse("success")
    }

    Post("/upload", FileUploadActor.FileUploadRequest("invalid\nfasta\ncontent")) ~> route ~> check {
      responseAs[FileUploadActor.FileUploadResponse] shouldEqual FileUploadActor.FileUploadResponse("success")
    }

  }
}
