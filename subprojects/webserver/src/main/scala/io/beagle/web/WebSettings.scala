package io.beagle.web

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.DbConfig
import io.beagle.search.SearchSettings
import io.beagle.security.SecuritySettings

case class WebSettings(uiRoot: String, port: Int, db: DbConfig, search: SearchSettings, security: SecuritySettings, exec: Exec) {

  def environment(): IO[WebEnv] = WebEnv.from(this)

}
