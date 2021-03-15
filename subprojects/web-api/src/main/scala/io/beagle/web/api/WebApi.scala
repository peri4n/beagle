package io.beagle.web.api

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.{OpenAPI, Server}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object WebApi extends App {

  val docs: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(UserApi.create, "My Bookshop", "1.0")
    .servers(List(Server("http://localhost:8080").description("Development server")))

  Files.write(Paths.get("openapi.yaml"), docs.toYaml.getBytes(StandardCharsets.UTF_8))
}
