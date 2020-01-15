package io.beagle.components

import cats.data.Reader
import io.beagle.Env
import io.beagle.service.{DatasetService, ProjectService, UserService}

sealed trait Service {

  def user: UserService

  def project: ProjectService

  def dataset: DatasetService

}

object Service {

  val services = Reader[Env, Service](_.services)

  def user = services map { _.user }

  def project = services map { _.project }

  def dataset = services map { _.dataset }

  case class DefaultService(env: Env) extends Service {

    lazy val user = UserService.instance(env)

    lazy val project = ProjectService.instance(env)

    lazy val dataset = DatasetService.instance(env)
  }

}
