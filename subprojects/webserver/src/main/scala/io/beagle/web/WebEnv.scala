package io.beagle.web

import cats.effect.IO
import cats.implicits._
import io.beagle.exec.Exec
import io.beagle.persistence.PersistenceEnv
import io.beagle.search.SearchEnv
import io.beagle.security.{SecurityEnv, SecuritySettings}
import io.beagle.web.controller._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.GZip

case class WebEnv(uiRoot: String, port: Int, persistence: PersistenceEnv, search: SearchEnv, security: SecurityEnv, exec: Exec) {

  /**
   * Runtime
   */
  implicit lazy val cs = exec.threadPool
  implicit lazy val timer = exec.timer

  /**
   * Services
   */
  lazy val searchService = search.searchService

  /**
   * Controllers
   */
  lazy val userRoute = UserController(persistence).route
  lazy val datasetRoute = DatasetController(persistence).route
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
      persistence <- settings.persistence.environment()
      search <- settings.search.environment()
      security <- settings.security.environment()
    } yield WebEnv(settings.uiRoot, settings.port, persistence, search, security, settings.exec)
}
