lazy val dependencies = new {
  val doobieV = "0.10.0"
  val testcontainerV = "1.15.2"
  val liquibaseV = "4.2.2"

  /** Doobie */
  val doobieCore = "org.tpolecat" %% "doobie-core" % doobieV
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieV
  val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieV
  val doobie = Seq(doobieCore, doobiePostgres, doobieHikari)

  /** Liquibase */
  val liquibaseCore = "org.liquibase" % "liquibase-core" % liquibaseV
  val liquibaseLogger = "com.mattbertolini" % "liquibase-slf4j" % "4.0.0" % Runtime
  val liquibase = Seq(liquibaseCore, liquibaseLogger)

  /** Test containers */
  val testcontainerPostgres = "org.testcontainers" % "postgresql" % testcontainerV % Test

}

libraryDependencies ++= dependencies.doobie
libraryDependencies ++= dependencies.liquibase
libraryDependencies += dependencies.testcontainerPostgres
