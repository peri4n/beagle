package io.beagle.environments

import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo

import scala.reflect.ClassTag

case class Test(name: String) extends Env {

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

    def seqset = Controllers.seqset.run(env)

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

object Test {
  def of[A: ClassTag] = Test(scala.reflect.classTag[A].runtimeClass.getSimpleName)
}
