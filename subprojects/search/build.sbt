// Elastic Search
val elastic4sVersion = "7.3.5"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-effect-cats" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json-circe" % elastic4sVersion
)

libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.36.0" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-elasticsearch" % "0.36.0" % "test"
