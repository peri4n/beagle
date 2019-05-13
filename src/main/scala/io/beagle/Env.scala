package io.beagle

import cats.data.Reader
import io.beagle.components.{AkkaComponent, ControllerComponent, SettingsComponent}

trait Env extends SettingsComponent with ControllerComponent with AkkaComponent

object Env {

  val env = Reader[Env, Env](identity)

  val settings = env map (_.settings)

  val route = env map (_.route)

  val system = env map (_.system)

  val materializer = env map (_.materializer)

}
