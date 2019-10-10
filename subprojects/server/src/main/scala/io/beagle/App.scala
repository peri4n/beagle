package io.beagle

import cats.effect.IO
import io.beagle.components.{Service, Settings}
import io.beagle.environments.Development
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect._

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    val environment = for {
      runMode <- detectRunMode
      settings <- loadSettings(runMode)
    } yield Development(settings)

    val env = environment.unsafeRunSync()

    implicit val timer = env.execution.timer
    implicit val cs = env.execution.threadPool

    val preconditions = for {
      _ <- Service.elasticSearch(env).connectionCheck()
      _ <- Service.elasticSearch(env).createSequenceIndex()
    } yield ()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(env.controllers.all.orNotFound)
      .resource

    val program = for {
      _ <- preconditions
      code <- server.use(_ => IO.never).start

    } yield code


    program.unsafeRunSync()
  }

  def detectRunMode: IO[RunMode] = IO(sys.env.get("mode")) map {
    case Some("production") => Prod
    case _                  => Dev
  }

  def loadSettings(runMode: RunMode) = getConfig(runMode).loadF[IO, Settings]

  def getConfig(runMode: RunMode) = ConfigSource.resources(runMode match {
    case Prod => "production.conf"
    case Dev  => "development.conf"
  }).withFallback(ConfigSource.resources("default.conf"))

  sealed trait RunMode
  case object Prod extends RunMode
  case object Dev extends RunMode
}

