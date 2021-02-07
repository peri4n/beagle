lazy val dependencies = new {
  val doobieV = "0.9.2"
  val testcontainerV = "0.39.0"
  val liquibaseV = "4.2.2"

  /** Doobie */
  val doobieCore = "org.tpolecat" %% "doobie-core" % doobieV
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieV
  val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieV
  val doobie = Seq(doobieCore, doobiePostgres, doobieHikari)

  /** Liquibase */
  val liquibaseCore = "org.liquibase" % "liquibase-core" % liquibaseV
  val liquibaseLogger = "com.mattbertolini" % "liquibase-slf4j" % "4.0.0" % "runtime"
  val liquibase = Seq(liquibaseCore, liquibaseLogger)

  /** Test containers */
  val testcontainerScalaTest = "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainerV % Test
  val testcontainerPostgres = "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainerV % Test

  val testcontainer = Seq(testcontainerScalaTest, testcontainerPostgres)

}

libraryDependencies ++= dependencies.doobie
libraryDependencies ++= dependencies.liquibase
libraryDependencies ++= dependencies.testcontainer


