package io.beagle.persistence.repository

import io.beagle.domain._
import io.beagle.persistence.repository.project.ProjectRepo
import io.beagle.persistence.testsupport.DbSuite

import java.time.LocalDateTime

class ProjectRepoTest extends DbSuite {

//    test("can store projects") {
//      val owner = User("name", "pw", "example@example.com")
//      val time = LocalDateTime.now()
//
//      val test = for {
//        ownerItem <- PostGresUserRepo.create(owner)
//        projectItem <- ProjectRepo.create(Project("project-name", ownerItem.id, time))
//      } yield projectItem
//
//      val maybeProject = test.transact(xa).unsafeRunSync()
//      maybeProject shouldBe ProjectItem(ProjectId(1), Project("project-name", UserId(1), time))
//    }
//  }
}
