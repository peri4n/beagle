package io.beagle.environments

import io.beagle.Env
import io.beagle.components._
import io.beagle.environments.execution.GlobalExecution
import io.beagle.environments.trancaction.JdbcTransaction

case class Production(settings: Settings) extends Env {

  env =>

  val controllers = Controller.DefaultController(env)

  val services = Service.DefaultService(env)

  val repositories = Repository.DevRepository()

  def security: Security = ???

  def transaction: Transaction = JdbcTransaction.instance(env)

  def execution: Execution = GlobalExecution
}
