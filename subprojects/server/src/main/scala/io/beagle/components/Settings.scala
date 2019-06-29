package io.beagle.components

trait Settings {

  def uiRoot: String

  def elasticSearch: ElasticSearchSettings

}

object Settings {

  val uiRoot = Env.settings map { _.uiRoot }

  val elasticSearch = Env.settings map { _.elasticSearch }

}
