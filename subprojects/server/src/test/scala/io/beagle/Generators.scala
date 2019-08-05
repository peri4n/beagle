package io.beagle

import io.beagle.domain.{Project, User, Seq}
import org.scalacheck.{Arbitrary, Gen}

object Generators {

  val email = for {
    preAt <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    domain <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    tld <- Gen.oneOf("com", "de", "us", "org")
  } yield preAt + "@" + domain + "." + tld

  implicit val userGenerator = Arbitrary(
    for {
      name <- Gen.alphaNumStr.suchThat(!_.isEmpty)
      email <- email
      password <- Gen.alphaNumStr
    } yield User(name, password, email))

  implicit val projectGenerator = Arbitrary(
    for {
      name <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    } yield Project(name)
  )

  implicit val dnaStringGenerator =
    for {
      list <- Gen.listOf(Gen.oneOf('A', 'C', 'G', 'T', 'a', 'c', 'g', 't'))
    } yield list.mkString

  implicit val sequenceGenerator = Arbitrary(
    for {
      name <- Gen.alphaNumStr.suchThat(!_.isEmpty)
      sequence <- dnaStringGenerator
    } yield Seq(name, sequence)
  )
}
