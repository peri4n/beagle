package io.beagle.components

import io.beagle.service.{ElasticSearchService, ProjectService, SeqService, UserService}

trait Services {

  def elasticSearch: ElasticSearchService

  def user: UserService

}

object Services {

  def elasticSearch = ElasticSearchService.instance

  def user = UserService.instance

  def project = ProjectService.instance

  def sequence = SeqService.instance

}
