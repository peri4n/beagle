name := "beagle"

version := "0.1"

scalaVersion := "2.12.8"

enablePlugins(JavaAppPackaging)

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
val akkaVersion = "2.5.23"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion, // actor logging
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test"
)

val akkaHttpVersion = "10.1.8"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
)

// Circe
libraryDependencies += "io.circe" %% "circe-generic" % "0.11.1"
libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.25.2"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

// Elastic Search
val elastic4sVersion = "6.5.1"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
)

// Scala test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"


fork := true
connectInput := true

javaOptions := Seq("-Dui.root=../frontend/dist/")
