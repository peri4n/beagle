lazy val dependencies = new {
  val doobieV = "0.9.2"
  val testcontainerV = "0.36.0"

  val doobieCore = "org.tpolecat" %% "doobie-core" % doobieV
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieV
  val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieV
  val doobie = Seq(doobieCore, doobiePostgres, doobieHikari)

  val testcontainerScalaTest = "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainerV % "test"
  val testcontainerPostgres = "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainerV % "test"
  val testcontainer = Seq(testcontainerScalaTest, testcontainerPostgres)
}

libraryDependencies ++= dependencies.doobie
libraryDependencies ++= dependencies.testcontainer


