name := "beagle"

version := "0.1"

scalaVersion := "2.12.8"

enablePlugins(JavaAppPackaging)

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.8"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.23"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23"

// Spray
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

// Elastic Search
val elastic4sVersion = "6.5.1"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion
)

fork := true

javaOptions := Seq("-Dui.root=../frontend/dist/")
