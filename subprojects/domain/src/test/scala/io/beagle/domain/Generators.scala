package io.beagle.domain

import org.scalacheck.{Arbitrary, Gen}

object Generators {

  val email: Gen[String] = for {
    preAt <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    domain <- Gen.alphaNumStr.suchThat(!_.isEmpty)
    tld <- Gen.oneOf("com", "de", "us", "org")
  } yield preAt + "@" + domain + "." + tld

  val userGenerator: Gen[User] =
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      email <- email
      password <- Gen.alphaNumStr
    } yield User(name, password, email)

  implicit val arbitraryUser: Arbitrary[User] = Arbitrary(userGenerator)

  implicit val projectGenerator: Arbitrary[Project] = Arbitrary(
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      id <- Gen.posNum[Long]
    } yield Project(name, UserId(id))
  )

  implicit val datasetGenerator: Arbitrary[Dataset] = Arbitrary(
    for {
      name <- Gen.alphaNumStr
        .suchThat(!_.isEmpty)
      ownerId <- Gen.posNum[Long]
      projectId <- Gen.posNum[Long]
    } yield Dataset(name, UserId(ownerId), ProjectId(projectId))
  )
  implicit val dnaStringGenerator: Gen[String] =
    for {
      list <- Gen.listOf(Gen.oneOf('A', 'C', 'G', 'T', 'a', 'c', 'g', 't'))
    } yield list.mkString

  implicit val sequenceGenerator: Arbitrary[Seq] = Arbitrary(
    for {
      name <- Gen.alphaNumStr.suchThat(!_.isEmpty)
      sequence <- dnaStringGenerator
    } yield Seq(name, sequence)
  )
}
