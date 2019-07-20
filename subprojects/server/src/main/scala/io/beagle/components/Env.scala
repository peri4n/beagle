package io.beagle.components

import cats.data.Reader
import io.beagle.environments.{Development, Production}

trait Env extends SettingsComponent with ControllerComponent with ServiceComponent with RepositoryComponent

object Env {

  def autoDetect = Option(System.getProperty("mode")).map(_.toLowerCase) match {
    case Some("production")  => Production
    case Some("development") => Development
    case _                   => Development
  }

  def settings = env map { _.settings }

  def controllers = env map { _.controllers }

  def services = env map { _.services }

  def repositories = env map { _.repositories }

  def env = Reader[Env, Env](identity)

}
