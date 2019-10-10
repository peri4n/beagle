package io.beagle.components

import io.beagle.Env
import io.beagle.service.{DatasetService, ElasticSearchService, ProjectService, UserService}

sealed trait Service {

  def elasticSearch: ElasticSearchService

  def user: UserService

}

object Service {

  def elasticSearch = ElasticSearchService.instance

  def user = UserService.instance

  def project = ProjectService.instance

  def dataset = DatasetService.instance

  def sequence = ???

  case class DefaultService(env: Env) extends Service {
    lazy val elasticSearch = Service.elasticSearch(env)

    lazy val user = Service.user(env)
  }

}
