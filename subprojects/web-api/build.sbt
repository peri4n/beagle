lazy val dependencies = new {

  val tapirV = "0.17.16"

  // tapir
  val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirV
  val tapirCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV
  val tapirOpenApi = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirV
  val tapirOpenApiCirce = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirV
  val tapir = Seq(tapirCore, tapirCirce, tapirOpenApi, tapirOpenApiCirce)

}

libraryDependencies ++= dependencies.tapir

