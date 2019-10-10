package io.beagle

import cats.data.Reader
import io.beagle.components._

trait Env extends SettingsComponent
  with ExecutionComponent
  with TransactionComponent
  with RepositoryComponent
  with ServiceComponent
  with SecurityComponent
  with ControllerComponent

object Env {

  def settings = env map { _.settings }

  def execution = env map { _.execution }

  def transaction = env map { _.transaction }

  def controllers = env map { _.controllers }

  def services = env map { _.services }

  def repositories = env map { _.repositories }

  def env = Reader[Env, Env](identity)

}
