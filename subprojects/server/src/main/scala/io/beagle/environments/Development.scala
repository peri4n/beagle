package io.beagle.environments

import com.typesafe.config.ConfigFactory
import io.beagle.components._
import io.beagle.repository.dataset.DatasetRepo
import io.beagle.repository.project.ProjectRepo
import io.beagle.repository.seq.SeqRepo
import io.beagle.repository.user.UserRepo


case object Development extends Env {

  env =>

  override val settings = new Settings {

    private val config = ConfigFactory.load("development.conf")

    def uiRoot = config.getString("ui.root")

    val elasticSearch = new ElasticSearchSettings {

      val protocol = config.getString("elasticsearch.protocol")

      val host = config.getString("elasticsearch.host")

      val port = config.getInt("elasticsearch.port")

    }
  }

  val controllers = new Controllers {

    def seqset = Controllers.dataset(env)

    def upload = Controllers.upload(env)

    def health = Controllers.health(env)

    def search = Controllers.search(env)

    def static = Controllers.static(env)
  }

  val services = new Services {
    val elasticSearch = Services.elasticSearch(env)

    val user = Services.user(env)

  }

  val repositories = new Repositories {

    def dataset = DatasetRepo.inMemory

    def sequence: SeqRepo = SeqRepo.inMemory

    def user: UserRepo = UserRepo.inMemory

    def project: ProjectRepo = ProjectRepo.inMemory
  }
}
