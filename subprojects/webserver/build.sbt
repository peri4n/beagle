// config
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.1"
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.12.1"

// Webapp
val http4sVersion = "0.21.20"
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-circe" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion

// Circe
libraryDependencies += "io.circe" %% "circe-generic" % "0.13.0"

fork := true
connectInput := true

