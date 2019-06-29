package io.beagle.components

import io.beagle.service.ElasticSearchService

trait Services {

  def elasticSearch: ElasticSearchService

}

object Services {

  val elasticSearch = Env.services map { _.elasticSearch }
}
