scalaVersion := "2.13.1"

// config
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.1"
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.12.1"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
val http4sVersion = "0.21.0"
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-circe" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion

// Circe
libraryDependencies += "io.circe" %% "circe-generic" % "0.13.0"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0"

// doobie
libraryDependencies += "org.tpolecat" %% "doobie-core" % "0.8.4"
libraryDependencies += "org.tpolecat" %% "doobie-postgres" % "0.8.4"
libraryDependencies += "org.tpolecat" %% "doobie-hikari" % "0.8.4"

// security
libraryDependencies += "com.pauldijou" %% "jwt-circe" % "4.2.0"

// Elastic Search
val elastic4sVersion = "7.3.1"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-effect-cats" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion,
  //  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
)

// Scala test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.8.3" % "test"
libraryDependencies += "org.specs2" %% "specs2-cats" % "4.8.3" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"

fork := true
connectInput := true

configs(InMem, InDb)

lazy val InMem = config("InMem") extend (Test)
lazy val InDb = config("InDb") extend (Test)

inConfig(InMem)(Defaults.testTasks ++ Seq(forkOptions := Defaults.forkOptionsTask.value))
inConfig(InDb)(Defaults.testTasks ++ Seq(forkOptions := Defaults.forkOptionsTask.value))

InMem / javaOptions += "-Dmode=mem"
InDb / javaOptions += "-Dmode=db"
