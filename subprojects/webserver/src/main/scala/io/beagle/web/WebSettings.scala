package io.beagle.web

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.PostgresConfig
import io.beagle.search.SearchConfig
import io.beagle.security.SecuritySettings

case class WebSettings(uiRoot: String, port: Int, db: PostgresConfig, search: SearchConfig, security: SecuritySettings, exec: Exec) {

  def environment(): IO[WebEnv] = WebEnv.from(this)

}
