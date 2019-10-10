package io.beagle.environments

import io.beagle.Env
import io.beagle.components._
import io.beagle.environments.execution.GlobalExecution
import io.beagle.environments.trancaction.JdbcTransaction

import scala.reflect.ClassTag

case class TestEnv(name: String) extends Env {

  env =>

  val settings = ???

  val execution: Execution = GlobalExecution

  val transaction: Transaction = JdbcTransaction.instance(env)

  val controllers = Controller.DefaultController(env)

  val services = Service.DefaultService(env)

  val repositories =
    if (System.getProperty("dbMode", "db") == "mem")
      Repository.ProdRepository()
    else
      Repository.DevRepository()

  def security: Security = Security.DefaultSecurity(env)

}

object TestEnv {
  def of[A: ClassTag] = TestEnv(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
