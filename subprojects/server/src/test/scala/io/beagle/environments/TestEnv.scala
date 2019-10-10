package io.beagle.environments

import io.beagle.Env
import io.beagle.components._

import scala.reflect.ClassTag

case class TestEnv(name: String) extends Env {

  env =>

  val settings = ???

  lazy val controllers = Controller.DefaultController(env)

  val services = Service.DefaultService(env)

  lazy val repositories =
    if (System.getProperty("dbMode", "db") == "mem")
      Repository.ProdRepository()
    else
      Repository.DevRepository()

  def security: Security = ???

  def transaction: Transaction = ???

  def execution: Execution = ???
}

object TestEnv {
  def of[A: ClassTag] = TestEnv(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
