package io.beagle.environments

import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo
import io.beagle.repository.project.ProjectRepo
import io.beagle.repository.seq.SeqRepo
import io.beagle.repository.user.UserRepo

import scala.reflect.ClassTag

case class TestEnv(name: String) extends Env {

  env =>

  lazy val settings = new Settings {

    val uiRoot: String = "dist"

    val elasticSearch = new ElasticSearchSettings {
      override val sequenceIndex = s"${ name.toLowerCase() }-${ System.currentTimeMillis() }-fasta"

      val protocol = "http"

      val host = "localhost"

      val port = 9200
    }
  }

  lazy val controllers = new Controllers {

    def seqset = Controllers.dataset(env)

    def upload = Controllers.upload(env)

    def health = Controllers.health(env)

    def search = Controllers.search(env)

    def static = ??? // the UI is served by a dedicated web server
  }

  lazy val services = new Services {
    val elasticSearch = Services.elasticSearch(env)

    val user = Services.user(env)

  }

  lazy val repositories = new Repositories {

    def dataset =
      if (System.getProperty("dbMode", "db") == "mem")
        DatasetRepo.inMemory
      else
        DatasetRepo.inDB

    def sequence: SeqRepo = SeqRepo.inMemory

    def user: UserRepo =
      if (System.getProperty("dbMode", "db") == "mem")
        UserRepo.inMemory
      else
        UserRepo.inDB

    def project: ProjectRepo =
      if (System.getProperty("dbMode", "db") == "mem")
        ProjectRepo.inMemory
      else
        ProjectRepo.inDB
  }
}

object TestEnv {
  def of[A: ClassTag] = TestEnv(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
