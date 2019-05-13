name := "beagle"

version := "0.1"

scalaVersion := "2.12.8"

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Webapp
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"

// Spray
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"

// Elastic Search connector for Akka
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "1.0.0"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
