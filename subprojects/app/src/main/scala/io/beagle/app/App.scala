package io.beagle.app

import cats.effect.{ExitCode, IO, IOApp, Sync}
import doobie.implicits._
import io.beagle.domain.{User, UserItem}
import io.beagle.persistence.DB
import io.beagle.web.server.{WebEnv, WebSettings}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object App extends IOApp {

  implicit def unsafeLogger[F[_] : Sync] = Slf4jLogger.getLogger[F]

  def run(args: List[String]): IO[ExitCode] = {

    for {
      _ <- Logger[IO].info("Welcome to bIO - the search engine for biological sequences")
      _ <- printRunMode
      environment <- loadWebserver

      searchService = environment.searchService

      esSetup = for {
        _ <- Logger[IO].info("Setting up elastic search environment")
        _ <- searchService.connectionCheck()
        _ <- searchService.createSequenceIndex()
      } yield ()

      db = environment.persistence

      dbSetup = for {
        _ <- Logger[IO].info("Setting up database environment")
        _ <- db.initSchema()
        _ <- injectAdmin(db)
      } yield ()

      setup = IO.racePair(esSetup, dbSetup)

      program = for {
        _ <- setup
        _ <- Logger[IO].info("Starting web server")
        _ <- environment.server.use(_ => IO.never).start
      } yield ExitCode.Success

    } yield program.unsafeRunSync()
  }

  private def injectAdmin(db: DB): IO[UserItem] = {
    val admin = User("admin", "admin", "admin@beagle.io")
    db.userService.create(admin).transact(db.xa)
  }

  def loadWebserver: IO[WebEnv] =
    ConfigSource.defaultApplication.load[WebSettings] match {
      case Left(error) => Logger[IO].error(error.toString) *> IO.raiseError(new RuntimeException(s"Unable to load config file: $error"))
      case Right(settings) => settings.environment()
    }

  def printRunMode: IO[Unit] = {
    sys.props.getOrElse("run.mode", "dev") match {
      case "dev" => Logger[IO].info("Running in development mode.")
      case "prod"  =>Logger[IO].info("Running in production mode.")
    }
  }
}
