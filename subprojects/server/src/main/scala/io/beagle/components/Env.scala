package io.beagle.components

import cats.data.Reader
import io.beagle.environments.{Development, Production}

trait Env extends SettingsComponent with ControllerComponent with ServiceComponent

object Env {

  def selection = Option(System.getProperty("mode")).map(_.toLowerCase) match {
    case Some("production") => Production
    case Some("development") => Development
    case _ => Development
  }

  val env = Reader[Env, Env](identity)

  val settings = env map { _.settings }

  val elasticSearchSettings = settings map { _.elasticSearch }

  val controllers = env map { _.controllers }

  val services = env map { _.services }

}
