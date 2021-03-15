package io.beagle.web.server.controller.json

import io.circe.generic.AutoDerivation
import org.http4s.circe.{CirceEntityDecoder, CirceEntityEncoder}

trait JsonSupport extends CirceEntityEncoder with CirceEntityDecoder with AutoDerivation
