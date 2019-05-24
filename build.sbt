name := "beagle"

version := "0.1"

scalaVersion := "2.12.8"

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"

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

// Swagger
val swaggerVersion = "2.0.8"
libraryDependencies ++= Seq(
  "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.2",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.0.3",
  "io.swagger.core.v3" % "swagger-core" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-annotations" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-models" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-jaxrs2" % swaggerVersion,
)
