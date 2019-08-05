package io.beagle.service

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Generators._
import io.beagle.components.Services
import io.beagle.domain.Project
import io.beagle.environments.TestEnv
import io.beagle.service.ProjectService.{ProjectAlreadyExists, ProjectDoesNotExist}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class ProjectServiceSpec extends FunSpec with GeneratorDrivenPropertyChecks with Matchers {

  val environment = TestEnv.of[SeqService]

  val service = Services.project(environment)

  def run[A](cio: ConnectionIO[A]): A = {
    cio.transact(environment.settings.database.transactor).unsafeRunSync()
  }

  describe("Creating projects") {
    it("can create new projects") {
      forAll { project: Project =>
        run(service.create(project)).project should be(project)
      }
    }

    it("fails if a project with the same name is already present") {
      forAll { project: Project =>
        val test = for {
          _ <- service.create(project)
          _ <- service.create(project)
        } yield ()

        an[ProjectAlreadyExists] should be thrownBy run(test)
      }
    }
  }

  describe("Updating projects") {
    it("can update existing projects") {
      forAll { project: Project =>
        val test = for {
          _ <- service.create(project)
          newItem <- service.update(project, project.copy(name = "foo"))
        } yield newItem

        run(test).project should be(project.copy(name = "foo"))
      }
    }
  }

  it("can delete an already present project") {
    forAll { project: Project =>
      val test = for {
        _ <- service.create(project)
        _ <- service.delete(project)
      } yield ()

      run(test)
    }
  }

  it("fails to delete if project is not present") {
    forAll { project: Project =>
      val test = service.delete(project)

      an[ProjectDoesNotExist] should be thrownBy run(test)
    }
  }
}
