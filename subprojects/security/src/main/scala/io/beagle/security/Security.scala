package io.beagle.security

sealed trait Security {

  def settings: SecuritySettings

  def jwt: Jwt

  def tokenStore: TokenStore
}

