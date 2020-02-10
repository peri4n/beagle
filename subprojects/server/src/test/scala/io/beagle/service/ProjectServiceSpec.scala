package io.beagle.service

import cats.Id
import cats.effect.IO
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.Env.TestEnv
import io.beagle.Generators._
import io.beagle.components.Service
import io.beagle.domain.{Project, User}
import io.beagle.service.ProjectService.{ProjectAlreadyExists, ProjectDoesNotExist}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ProjectServiceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks with Matchers with BeforeAndAfter {

  val setup: IO[(TestEnv, Id[UserService], Id[ProjectService])] = for {
    environment <- TestEnv.of[ProjectServiceSpec]
    userService = Service.user(environment)
    projectService = Service.project(environment)
  } yield (environment, userService, projectService)

  val (environment, userService, projectService) = setup.unsafeRunSync()

  def run[A](cio: ConnectionIO[A]): A = {
    cio.transact(environment.persistence.transactor).unsafeRunSync()
  }

  after {
    run(for {
      _ <- projectService.deleteAll()
      _ <- userService.deleteAll()
    } yield ())
  }

  describe("Creating projects") {
    it("succeeds if all requirements are met") {
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
    it("succeeds if all requirements are met") {
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
