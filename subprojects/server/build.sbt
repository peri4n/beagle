name := "beagle"

version := "0.1"

scalaVersion := "2.13.1"

enablePlugins(JavaAppPackaging)

// config
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.1"
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.12.1"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
val http4sVersion = "0.21.0-M5"
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-circe" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-testing" % http4sVersion % "test"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion

// Circe
libraryDependencies += "io.circe" %% "circe-generic-simple" % "0.12.2"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

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
libraryDependencies += "org.specs2" %% "specs2-core" % "4.7.1" % "test"
//libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"

fork := true
connectInput := true

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  //  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  //  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

lazy val InMem = config("InMem") extend (Test)
lazy val InDb = config("InDb") extend (Test)

lazy val root = (project in file("."))
  .configs(InMem, InDb)
  .settings(
    inConfig(InMem)(Defaults.testTasks ++ Seq(forkOptions := Defaults.forkOptionsTask.value)),
    inConfig(InDb)(Defaults.testTasks ++ Seq(forkOptions := Defaults.forkOptionsTask.value)),
    InMem / javaOptions += "-Dmode=mem",
    InDb / javaOptions += "-Dmode=db"
  )

