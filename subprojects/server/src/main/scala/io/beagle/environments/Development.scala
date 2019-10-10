package io.beagle.environments

import io.beagle.Env
import io.beagle.components._
import io.beagle.environments.execution.GlobalExecution
import io.beagle.environments.trancaction.JdbcTransaction

case class Development(settings: Settings) extends Env {

  env =>

  val execution: Execution = GlobalExecution

  val transaction: Transaction = JdbcTransaction.instance(env)

  val repositories = Repository.DevRepository()

  val services = Service.DefaultService(env)

  val security: Security = Security.DefaultSecurity(env)

  val controllers = Controller.DefaultController(env)

}
