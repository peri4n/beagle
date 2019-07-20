package io.beagle.service

import org.specs2.mutable.Specification

class UserServiceSpec extends Specification {

  "The UserService" should {
    "can create new users" in {
      ok
    }
    "fails to create if a user is already present" in {
      ok
    }
    "can delete a already present user" in {
      ok
    }
    "fails to delete if a user is not present" in {
      ok
    }
  }
}
