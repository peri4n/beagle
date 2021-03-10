lazy val dependencies = new {

  val http4sV = "0.21.20"
  val tapirV = "0.17.15"

  // web server
  val http4sCore = "org.http4s" %% "http4s-core" % http4sV
  val http4sDsl =  "org.http4s" %% "http4s-dsl" % http4sV
  val http4sCirce = "org.http4s" %% "http4s-circe" % http4sV
  val http4sServer = "org.http4s" %% "http4s-blaze-server" % http4sV
  val http4s = Seq(http4sCore, http4sDsl, http4sCirce, http4sServer)

  // openapi
  val tapir = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV
}

libraryDependencies ++= dependencies.http4s
libraryDependencies += dependencies.tapir

fork := true
connectInput := true

