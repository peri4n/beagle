package io.beagle.web

import cats.effect.IO
import io.beagle.exec.Exec
import io.beagle.persistence.PgConfig
import io.beagle.search.ElasticSearchConfig
import io.beagle.security.SecuritySettings

case class WebSettings(uiRoot: String, port: Int, db: PgConfig, search: ElasticSearchConfig, security: SecuritySettings, exec: Exec) {

  def environment(): IO[WebEnv] = WebEnv.from(this)

}
