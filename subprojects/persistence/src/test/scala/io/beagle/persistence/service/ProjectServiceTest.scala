package io.beagle.persistence.service

import doobie.implicits._
import io.beagle.domain.Generators._
import io.beagle.domain.{Project, User}
import io.beagle.persistence.service.testsupport.ForAllPostgresContainer
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ProjectServiceTest extends ForAllPostgresContainer with ScalaCheckPropertyChecks with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  override def beforeAll = {
    (for {
      _ <- UserService.createTable()
      _ <- ProjectService.createTable()
    } yield ()).transact(xa).unsafeRunSync()
  }

  after {
    (for {
      _ <- ProjectService.deleteAll()
      _ <- UserService.deleteAll()
    } yield ()).transact(xa).unsafeRunSync()
  }

  describe("A project service") {
    describe("Create projects") {
      it("succeeds if all requirements are met") {
        forAll { (project: Project, user: User) =>
          val test = for {
            owner <- UserService.create(user)
            projectWithOwner = project.copy(ownerId = owner.id)
            project <- ProjectService.create(projectWithOwner)
          } yield project

          test.transact(xa).unsafeRunSync().project.name should be(project.name)
        }
      }
    }
  }
}
//      "fails if a project with the same name is already present" in {
//        prop { (project: Project, user: User) =>
//          val test = for {
//            owner <- UserService.create(user)
//            projectWithOwner = project.copy(ownerId = owner.id)
//            _ <- ProjectService.create(projectWithOwner)
//            _ <- ProjectService.create(projectWithOwner)
//          } yield ()
//
//          run(test) should throwAn[ProjectAlreadyExists]
//        }
//      }
//    }
//
//    "Update projects" in {
//      "succeeds if all requirements are met" in {
//        prop { (project: Project, user: User) =>
//          val test = for {
//            owner <- UserService.create(user)
//            projectWithOwner = project.copy(ownerId = owner.id)
//            _ <- ProjectService.create(projectWithOwner)
//            newItem <- ProjectService.update(projectWithOwner, projectWithOwner.copy(name = "foo"))
//          } yield newItem
//
//          run(test).project.name shouldEqual "foo"
//        }
//      }
//    }
//
//    "Delete projects" in {
//      "can delete an already present project" in {
//        prop { (project: Project, user: User) =>
//          val test = for {
//            userItem <- UserService.create(user)
//            projectWithOwner = project.copy(ownerId = userItem.id)
//            _ <- ProjectService.create(projectWithOwner)
//            _ <- ProjectService.delete(project, user)
//          } yield true
//
//          run(test) shouldEqual true
//        }
//      }
//      "fails to delete if project is not present" in {
//        prop { (project: Project, user: User) =>
//          val test = for {
//            userItem <- UserService.create(user)
//            projectWithOwner = project.copy(ownerId = userItem.id)
//            _ <- ProjectService.delete(projectWithOwner, user)
//          } yield ()
//
//          run(test) should throwA[ProjectDoesNotExist]
//        }
//      }
//    }
//  }
