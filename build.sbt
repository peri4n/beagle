import sbt.Keys.{javaOptions, libraryDependencies}

lazy val beagle = project
  .in(file("."))
  .aggregate(
    frontend,
    webserver
  )

lazy val app = project
  .in(file("subprojects/app"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    webserver % "compile->compile;runtime->runtime;test->test",
  )

lazy val webserver = project
  .in(file("subprojects/webserver"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    search % "compile->compile;test->test",
    persistence % "compile->compile;runtime->runtime;test->test",
    execution % "compile->compile;test->test",
    parser,
    security)

lazy val persistence = project
  .in(file("subprojects/persistence"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(domain % "compile->compile;test->test", execution)

lazy val execution = project
  .in(file("subprojects/execution"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )

lazy val search = project
  .in(file("subprojects/search"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(domain, execution)

lazy val security = project
  .in(file("subprojects/security"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(persistence, execution)

lazy val parser = project
  .in(file("subprojects/parser"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )

lazy val domain = project
  .in(file("subprojects/domain"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )

lazy val frontend = project
  .in(file("subprojects/frontend"))

lazy val runMode = settingKey[String]("Detects running mode")

lazy val commonSettings = Seq(
  scalaVersion := "2.13.5",
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  Test / parallelExecution := false,
  testFrameworks += new TestFramework("munit.Framework")
) ++ runModeSettings

lazy val runModeSettings = Seq(
  runMode := sys.props.get("run.mode").getOrElse("dev"),
  javaOptions += {
    s"-Drun.mode=${ runMode.value }"
  }
)

lazy val compilerOptions = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:postfixOps", // Allow postfix operations
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

lazy val dependencies = new {

  val checkV = "1.14.0"
  val catsV = "2.2.0"
  val logbackV = "1.2.3"
  val catsLogV = "1.2.0"
  val munitV = "0.13.1"
  val pureConfigV = "0.14.0"

  // configuration
  val pureConfig = "com.github.pureconfig" %% "pureconfig" % pureConfigV

  // logging
  val logging = "org.typelevel" %% "log4cats-slf4j" % catsLogV
  val logback = "ch.qos.logback" % "logback-classic" % logbackV

  // cats
  val catsCore = "org.typelevel" %% "cats-core" % catsV
  val catsEffect = "org.typelevel" %% "cats-effect" % catsV

  // Scala test
  val scalaCheck = "org.scalacheck" %% "scalacheck" % checkV % Test
  val munit = "org.typelevel" %% "munit-cats-effect-2" % munitV % Test
}

lazy val commonDependencies = Seq(
  // configuration
  dependencies.pureConfig,

  // logging
  dependencies.logging,
  dependencies.logback,

  // cats
  dependencies.catsCore,
  dependencies.catsEffect,

  // tests
  dependencies.munit,
  dependencies.scalaCheck
)


