package io.beagle.service

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Env.TestEnv
import io.beagle.Generators._
import io.beagle.components.Service
import io.beagle.domain.{Dataset, Project, User}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class DatasetServiceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks with Matchers with BeforeAndAfter {

  val setup = for {
    environment <- TestEnv.of[DatasetServiceSpec]
    userService = Service.user(environment)
    projectService = Service.project(environment)
    datasetService = Service.dataset(environment)
  } yield (environment, userService, projectService, datasetService)

  val (environment, userService, projectService, datasetService) = setup.unsafeRunSync()

  def run[A](cio: ConnectionIO[A]): A = cio.transact(environment.persistence.transactor).unsafeRunSync()

  after {
    run(for {
      _ <- datasetService.deleteAll()
      _ <- projectService.deleteAll()
      _ <- userService.deleteAll()
    } yield ())
  }

  describe("Creating datasets") {
    it("succeeds if all requirements are met") {
      forAll { (dataset: Dataset, project: Project, user: User) =>
        val test = for {
          owner <- userService.create(user)
          projectWithOwner = project.copy(ownerId = owner.id)
          project <- projectService.create(projectWithOwner)
          datasetWithProject = dataset.copy(projectId = project.id)
          dataset <- datasetService.create(datasetWithProject)
        } yield dataset

        run(test).dataset.name should be(dataset.name)
      }
    }
  }
}
