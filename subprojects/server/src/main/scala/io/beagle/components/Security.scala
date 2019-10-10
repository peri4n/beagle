package io.beagle.components

import io.beagle.Env
import io.beagle.security.BasicAuthenticator

sealed trait Security {

  def basicAuth: BasicAuthenticator

}

object Security {

  def basicAuth = BasicAuthenticator.instance

  case class DefaultSecurity(env: Env) extends Security {

    val basicAuth: BasicAuthenticator = Security.basicAuth(env)

  }

}
