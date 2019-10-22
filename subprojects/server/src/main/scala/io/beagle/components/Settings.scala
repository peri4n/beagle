package io.beagle.components

import io.beagle.Env
import io.beagle.components.persistence.PersistenceSettings
import io.beagle.components.settings.{ElasticSearchSettings, JwtSettings, SecuritySettings}

import scala.concurrent.duration._

sealed trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

  def security: SecuritySettings

}

object Settings {

  def uiRoot = Env.settings map { _.uiRoot }

  def elasticSearch = Env.settings map { _.elasticSearch }

  def security = Env.settings map { _.security }

  case class Development(uiRoot: String,
                         elasticSearch: ElasticSearchSettings,
                         security: SecuritySettings
                        ) extends Settings

  case class Test(name: String ) extends Settings {
    val uiRoot = "ignored"

    val security = SecuritySettings("realm", JwtSettings(30.minutes, "secret"))

    val elasticSearch = ElasticSearchSettings.Local()
  }

}
