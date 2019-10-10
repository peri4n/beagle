package io.beagle

import cats.effect.IO
import cats.implicits._
import io.beagle.components.{Service, Settings}
import io.beagle.domain.User
import io.beagle.environments.Development
import org.http4s.implicits._
import doobie.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect._

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences")

    val environment = for {
      runMode <- detectRunMode
      settings <- loadSettings(runMode)
    } yield Development(settings)

    val env = environment.unsafeRunSync()
    Logger.info("Done creating the environment")

    implicit val timer = env.execution.timer
    implicit val cs = env.execution.threadPool
    implicit val xa = env.transaction.transactor

    val preconditions = for {
      _ <- env.services.user.create(User("admin", "admin", "admin@beagle.io")).transact(xa)
      _ <- Service.elasticSearch(env).connectionCheck()
      _ <- Service.elasticSearch(env).createSequenceIndex()
    } yield ()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(env.controllers.all.orNotFound)
      .resource

    (preconditions, server.use(_ => IO.never).start)
      .parMapN( (_, _) => () )
      .unsafeRunSync()
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

