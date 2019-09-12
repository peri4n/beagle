package io.beagle

import io.beagle.domain.{Dataset, Project, ProjectId, Seq, User, UserId}
import org.scalacheck.{Arbitrary, Gen}

object Generators {

  val email = for {
    preAt <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    domain <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    tld <- Gen.oneOf("com", "de", "us", "org")
  } yield preAt + "@" + domain + "." + tld

  val userGenerator =
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      email <- email
      password <- Gen.alphaNumStr
    } yield User(name, password, email)

  implicit val arbitraryUser = Arbitrary(userGenerator)

  implicit val projectGenerator = Arbitrary(
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      id <- Gen.posNum[Long]
    } yield Project(name, UserId(id))
  )

  implicit val datasetGenerator = Arbitrary(
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      id <- Gen.posNum[Long]
    } yield Dataset(name, ProjectId(id))
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
