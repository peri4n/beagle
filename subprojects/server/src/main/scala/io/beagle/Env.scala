package io.beagle

import cats.data.Reader
import io.beagle.components.Execution.GlobalExecution
import io.beagle.components._
import io.beagle.components.persistence.{Persistence, PersistenceComponent}

import scala.reflect._

sealed trait Env extends ExecutionComponent
  with PersistenceComponent
  with RepositoryComponent
  with ServiceComponent
  with SearchComponent
  with SecurityComponent
  with WebComponent

object Env {

  val env = Reader[Env, Env](identity)

  val execution = env map { _.execution }

  val persistence = env map { _.persistence }

  val controllers = env map { _.web }

  val services = env map { _.services }

  val search = env map { _.search }

  val repositories = env map { _.repositories }

  case class Development() extends Env {

    env =>

    val execution = GlobalExecution

    val persistence = Persistence.InMemoryPersistence(execution)

    val repositories = Repository.DevRepository()

    val services = Service.DefaultService(env)

    val search = Search.DefaultSearch(env)

    val security = Security.DefaultSecurity(env)

    val web = Web.DefaultWeb(env)

  }

  case class Production() extends Env {

    env =>

    val execution = GlobalExecution

    val persistence = Persistence.PostgresPersistence("beagle", "fbull", "", execution = execution)

    val repositories = Repository.DevRepository()

    val services = Service.DefaultService(env)

    val search = Search.DefaultSearch(env)

    val security = Security.DefaultSecurity(env)

    val web = Web.DefaultWeb(env)

  }

  case class TestEnv(name: String) extends Env {

    env =>

    val execution = GlobalExecution

    val persistence =
      if (System.getProperty("mode", "db") == "mem")
        Persistence.InMemoryPersistence(execution)
      else
        Persistence.PostgresPersistence(name, "fbull", "", execution = execution)

    val web = Web.DefaultWeb(env)

    val services = Service.DefaultService(env)

    val search = Search.DefaultSearch(env)

    val repositories =
      if (System.getProperty("mode", "db") == "mem")
        Repository.DevRepository()
      else
        Repository.ProdRepository()

    val security = Security.DefaultSecurity(env)

  }

  object TestEnv {
    def of[A: ClassTag] = {
      val env = TestEnv(classTag[A].runtimeClass.getSimpleName)
      env.persistence.hook.map( _ => env)
    }
  }

}
