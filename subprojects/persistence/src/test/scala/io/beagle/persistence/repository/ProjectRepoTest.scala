package io.beagle.persistence.repository

import doobie.implicits._
import io.beagle.domain._
import io.beagle.persistence.repository.project.ProjectRepo
import io.beagle.persistence.repository.user.UserRepo
import io.beagle.persistence.testsupport.DbSupport
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import java.time.LocalDateTime

class ProjectRepoTest extends AnyFunSpec with Matchers with OptionValues with BeforeAndAfterEach with DbSupport {

  override def beforeEach(): Unit = {
    (for {
      _ <- environment.userService.deleteAll()
      _ <- environment.projectService.deleteAll()
    } yield ())
      .transact(xa).unsafeRunSync()
  }

  describe("A ProjectRepo") {
    it("can store projects") {
      val owner = User("name", "pw", "example@example.com")
      val time = LocalDateTime.now()

      val test = for {
        ownerItem <- UserRepo.create(owner)
        projectItem <- ProjectRepo.create(Project("project-name", ownerItem.id, time))
      } yield projectItem

      val maybeProject = test.transact(xa).unsafeRunSync()
      maybeProject shouldBe ProjectItem(ProjectId(1), Project("project-name", UserId(1), time))
    }
  }
}
