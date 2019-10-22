package io.beagle.environments

import io.beagle.Env
import io.beagle.components._
import io.beagle.components.persistence.Persistence
import io.beagle.environments.execution.GlobalExecution

case class Production(settings: Settings) extends Env {

  env =>

  val execution = GlobalExecution

  val persistence = Persistence.instance(env)

  val repositories = Repository.DevRepository()

  val services = Service.DefaultService(env)

  val security = Security.DefaultSecurity(env)

  val controllers = Controller.DefaultController(env)

}
