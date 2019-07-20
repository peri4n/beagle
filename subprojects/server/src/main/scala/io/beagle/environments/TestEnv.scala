package io.beagle.environments

import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo

import scala.reflect.ClassTag

case class TestEnv(name: String) extends Env {

  env =>

  val settings = new Settings {

    val uiRoot: String = "dist"

    val elasticSearch = new ElasticSearchSettings {
      override val sequenceIndex = s"${ name.toLowerCase() }-${ System.currentTimeMillis() }-fasta"

      val protocol = "http"

      val host = "localhost"

      val port = 9200
    }
  }

  val controllers = new Controllers {

    def seqset = Controllers.dataset.run(env)

    def upload = Controllers.upload.run(env)

    def health = Controllers.health.run(env)

    def search = Controllers.search.run(env)

    def static = ??? // the UI is served by a dedicated web server
  }

  val services = new Services {
    def elasticSearch = Services.elasticSearch.run(env)
  }

  val repositories = new Repositories {
    val sequenceSet = DatasetRepo.inMemory
  }
}

object TestEnv {
  def of[A: ClassTag] = TestEnv(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
