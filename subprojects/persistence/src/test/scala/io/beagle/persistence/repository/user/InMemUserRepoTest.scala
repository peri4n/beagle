package io.beagle.persistence.repository.user

import doobie.implicits._
import io.beagle.domain.User
import io.beagle.exec.Exec.Global
import io.beagle.persistence.InMemDB
import munit.FunSuite

class InMemUserRepoTest extends FunSuite {

  test("can create a user") {
    val persistence = (for {
      userRepo <- InMemUserRepo.create()
      db = InMemDB(Global, userRepo, null, null)
    } yield db).unsafeRunSync()

    persistence.userRepo
      .create(User("name", "password", "test@example.com"))
      .transact(persistence.xa)
  }

}
