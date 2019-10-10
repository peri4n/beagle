package io.beagle.components

import io.beagle.Env
import io.beagle.components.settings.{DatabaseSettings, ElasticSearchSettings, SecuritySettings}

sealed trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

  def database: DatabaseSettings

  def security: SecuritySettings

}

object Settings {

  def uiRoot = Env.settings map { _.uiRoot }

  def elasticSearch = Env.settings map { _.elasticSearch }

  def database = Env.settings map { _.database }

  def security = Env.settings map { _.security }

  case class Development(uiRoot: String,
                         elasticSearch: ElasticSearchSettings,
                         database: DatabaseSettings,
                         security: SecuritySettings
                        ) extends Settings

}
