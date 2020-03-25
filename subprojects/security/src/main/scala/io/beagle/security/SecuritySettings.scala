package io.beagle.security

sealed trait SecuritySettings {
  def basicAuthRealm: String
}

