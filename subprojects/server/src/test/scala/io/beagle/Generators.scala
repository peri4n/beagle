package io.beagle

import io.beagle.domain.User
import org.scalacheck.{Arbitrary, Gen}

object Generators {

  val email = for {
    preAt <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    domain <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    tld <- Gen.oneOf("com", "de", "us", "org")
  } yield preAt + "@" + domain + "." + tld

  implicit val user = Arbitrary(for {
    name <- Gen.alphaNumStr
    email <- email
    password <- Gen.alphaNumStr
  } yield User(name, password, email))

}
