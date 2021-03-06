package io.beagle.web.server

import cats.effect.IO
import cats.implicits._
import io.beagle.exec.Exec
import io.beagle.persistence.DB
import io.beagle.search.ElasticSearch
import io.beagle.security.SecurityEnv
import io.beagle.web.server.controller._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.GZip

case class WebEnv(uiRoot: String, port: Int, persistence: DB, search: ElasticSearch, security: SecurityEnv, exec: Exec) {

  /**
   * Runtime
   */
  implicit lazy val cs = exec.shift
  implicit lazy val timer = exec.timer
  lazy val xa = persistence.xa

  /**
   * Services
   */
  lazy val searchService = search.searchService
  lazy val datasetService = persistence.datasetService
  lazy val userService = persistence.userService

  /**
   * Controllers
   */
  lazy val userRoute = UserController(userService, xa).route
  lazy val datasetRoute = DatasetController(datasetService, xa).route
  lazy val staticRoute = StaticContentController(exec, uiRoot).route
  lazy val searchRoute = SearchController(searchService).route
  lazy val healthRoute = HealthCheckController(searchService).route
  lazy val fileUploadRoute = FileUploadController(exec, searchService).route

  lazy val all = GZip(userRoute <+> datasetRoute <+> searchRoute <+> healthRoute <+> fileUploadRoute <+> staticRoute)

  lazy val server = BlazeServerBuilder[IO](executionContext = exec.context)
    .bindHttp(port)
    .withHttpApp(all.orNotFound)
    .resource
}

object WebEnv {
  def from(settings: WebSettings): IO[WebEnv] =
    for {
      security <- settings.security.environment()
      search = settings.search.environment(settings.exec)
      persistence <- settings.db.environment(settings.exec)
    } yield WebEnv(settings.uiRoot, settings.port, persistence, search, security, settings.exec)
}
