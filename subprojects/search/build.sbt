lazy val dependencies = new {
  // Elastic Search
  val elastic4sV = "7.3.5"
  val testcontainerV = "1.15.2"

  val elastic4s = Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sV,
    "com.sksamuel.elastic4s" %% "elastic4s-effect-cats" % elastic4sV,
    "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sV,
    "com.sksamuel.elastic4s" %% "elastic4s-json-circe" % elastic4sV
  )
  val testcontainerPostgres = "org.testcontainers" % "elasticsearch" % testcontainerV % Test
}

libraryDependencies ++= dependencies.elastic4s
libraryDependencies += dependencies.testcontainerPostgres
