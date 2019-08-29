package io.beagle.service

import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Generators._
import io.beagle.components.Services
import io.beagle.domain.{Project, User}
import io.beagle.environments.TestEnv
import io.beagle.service.ProjectService.{ProjectAlreadyExists, ProjectDoesNotExist}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class ProjectServiceSpec extends FunSpec with GeneratorDrivenPropertyChecks with Matchers with BeforeAndAfter {

  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSize = 0, sizeRange = 80)

  val environment = TestEnv.of[SeqService]

  val userService = Services.user(environment)

  val projectService = Services.project(environment)

  def run[A](cio: ConnectionIO[A]): A = {
    cio.transact(environment.settings.database.transactor).unsafeRunSync()
  }

  after {
    run(for {
      _ <- projectService.deleteAll()
      _ <- userService.deleteAll()
    } yield ())
  }

  describe("Creating projects") {
    it("can create new projects") {
      forAll { (project: Project, user: User) =>
        val test = for {
          owner <- userService.create(user)
          projectWithOwner = project.copy(ownerId = owner.id)
          project <- projectService.create(projectWithOwner)
        } yield project

        run(test).project.name should be(project.name)
      }
    }

    it("fails if a project with the same name is already present") {
      forAll { (project: Project, user: User) =>
        val test = for {
          owner <- userService.create(user)
          projectWithOwner = project.copy(ownerId = owner.id)
          _ <- projectService.create(projectWithOwner)
          _ <- projectService.create(projectWithOwner)
        } yield ()

        an[ProjectAlreadyExists] should be thrownBy run(test)
      }
    }
  }

  describe("Updating projects") {
    it("can update existing projects") {
      forAll { (project: Project, user: User) =>
        val test = for {
          owner <- userService.create(user)
          projectWithOwner = project.copy(ownerId = owner.id)
          _ <- projectService.create(projectWithOwner)
          newItem <- projectService.update(projectWithOwner, projectWithOwner.copy(name = "foo"))
        } yield newItem

        run(test).project.name should be("foo")
      }
    }
  }

  it("can delete an already present project") {
    forAll { (project: Project, user: User) =>
      val test = for {
        userItem <- userService.create(user)
        projectWithOwner = project.copy(ownerId = userItem.id)
        _ <- projectService.create(projectWithOwner)
        _ <- projectService.delete(project, user)
      } yield ()

      run(test)
    }
  }

  it("fails to delete if project is not present") {
    forAll { (project: Project, user: User) =>
      val test = for {
        userItem <- userService.create(user)
        projectWithOwner = project.copy(ownerId = userItem.id)
        _ <- projectService.delete(projectWithOwner, user)
      } yield ()

      an[ProjectDoesNotExist] should be thrownBy run(test)
    }
  }
}
