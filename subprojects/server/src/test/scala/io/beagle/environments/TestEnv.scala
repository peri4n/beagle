package io.beagle.environments

import io.beagle.Env
import io.beagle.components.Settings.Test
import io.beagle.components._
import io.beagle.components.persistence.Persistence.InMemoryPersistence
import io.beagle.environments.execution.GlobalExecution

import scala.reflect.ClassTag

case class TestEnv(name: String) extends Env {

  env =>

  val settings = Test(name)

  val execution = GlobalExecution

  val persistence = InMemoryPersistence(execution)

  val controllers = Controller.DefaultController(env)

  val services = Service.DefaultService(env)

  val repositories =
    if (System.getProperty("dbMode", "db") == "mem")
      Repository.ProdRepository()
    else
      Repository.DevRepository()

  def security = Security.DefaultSecurity(env)

}

object TestEnv {
  def of[A: ClassTag] = TestEnv(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
