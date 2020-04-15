package io.beagle.components

import cats.effect.IO
import cats.syntax.semigroupk._
import io.beagle.controller._
import io.beagle.exec.Execution
import io.beagle.persistence.Postgres
import io.beagle.search.Search
import io.beagle.security.Security
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.GZip

case class Web(uiRoot: String, port: Int, persistence: Postgres, search: Search, security: Security, execution: Execution) {

  /**
   * Runtime
   */
  implicit lazy val cs = execution.threadPool
  implicit lazy val timer = execution.timer

  /**
   * Services
   */
  lazy val searchService = Search.service(search)

  /**
   * Controllers
   */
  lazy val userRoute = UserController(persistence).route
  lazy val datasetRoute = DatasetController(persistence).route
  lazy val staticRoute = StaticContentController(execution, uiRoot).route
  lazy val searchRoute = SearchController(searchService).route
  lazy val healthRoute = HealthCheckController(searchService).route
  lazy val fileUploadRoute = FileUploadController(execution, searchService).route

  lazy val all = GZip(userRoute <+> datasetRoute <+> searchRoute <+> healthRoute <+> fileUploadRoute <+> staticRoute)

  lazy val server = BlazeServerBuilder[IO]
    .bindHttp(port)
    .withHttpApp(all.orNotFound)
    .resource
}

