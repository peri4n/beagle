package io.beagle.environments

import io.beagle.Env
import io.beagle.components._
import io.beagle.components.persistence.Persistence.InMemoryPersistence
import io.beagle.environments.execution.GlobalExecution

case class Development(settings: Settings) extends Env {

  env =>

  val execution = GlobalExecution

  val persistence = InMemoryPersistence(execution)

  val repositories = Repository.DevRepository()

  val services = Service.DefaultService(env)

  val security = Security.DefaultSecurity(env)

  val controllers = Controller.DefaultController(env)

}
