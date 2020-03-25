package io.beagle.app

import cats.effect.IO
import io.beagle.Env
import io.beagle.domain.User
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences")

    val environment = detectRunMode flatMap loadEnvironment

    val env = environment.unsafeRunSync()
    Logger.info("Done creating the environment")

    implicit val timer = env.execution.timer
    implicit val cs = env.execution.threadPool
    implicit val xa = env.persistence.transactor

    val createAdminUser = for {
      us <- Service.user(env)
      _ <- us.create(User("admin", "admin", "admin@beagle.io")).transact(xa)
    } yield ()

    val createSearchIndex = for {
      es <- Search.service(env)
      _ <- es.connectionCheck()
      _ <- es.createSequenceIndex()
    } yield ()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(env.web.all.orNotFound)
      .resource

    Logger.info("Starting webserver")
    val program = for {
      _ <- env.persistence.hook
      _ <- createAdminUser
      _ <- createSearchIndex
      _ <- server.use(_ => IO.never).start
    } yield ()

    program.unsafeRunSync()
  }

  def detectRunMode: IO[RunMode] = IO(sys.env.get("mode")) map {
    case Some("production") => Prod
    case _                  => Dev
  }

  private def loadEnvironment(runMode: RunMode) = getConfig(runMode).loadF[IO, Env]

  private def getConfig(runMode: RunMode) = ConfigSource.resources(runMode match {
    case Prod => "production.conf"
    case Dev  => "development.conf"
  }).withFallback(ConfigSource.resources("default.conf"))

  sealed trait RunMode
  case object Prod extends RunMode
  case object Dev extends RunMode
}
