package io.beagle.components

import io.beagle.service.ElasticSearchService

trait Services {

  def elasticSearch: ElasticSearchService

}

object Services {

  def elasticSearch = ElasticSearchService.instance

}
