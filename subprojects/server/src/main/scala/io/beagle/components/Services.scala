package io.beagle.components

import io.beagle.service.{DatasetService, ElasticSearchService, ProjectService, UserService}

trait Services {

  def elasticSearch: ElasticSearchService

  def user: UserService

}

object Services {

  def elasticSearch = ElasticSearchService.instance

  def user = UserService.instance

  def project = ProjectService.instance

  def dataset = DatasetService.instance

  def sequence = ???

}
