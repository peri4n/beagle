package io.beagle.app

import cats.effect.{ExitCode, IO, IOApp, Sync}
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import io.beagle.domain.User
import io.beagle.persistence.PersistenceEnv
import io.beagle.web.{WebEnv, WebSettings}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
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
        _ <- db.createDb()
        _ <- db.createTables()
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

  private def injectAdmin(persistenceEnv: PersistenceEnv): IO[Unit] = {
    val admin = User("admin", "admin", "admin@beagle.io")

    val create = for {
      maybeUser <- persistenceEnv.userService.findByName(admin.name)
      _ <- maybeUser.fold(persistenceEnv.userService.create(admin).map(_ => ())) {
        _ => Sync[ConnectionIO].unit
      }
    } yield ()
    create.transact(persistenceEnv.transactor)
  }

  def loadWebserver: IO[WebEnv] =
    ConfigSource.defaultApplication.load[WebSettings] match {
      case Left(error) => Logger[IO].error(error.toString) *> IO.raiseError(new RuntimeException("foo"))
      case Right(settings) => settings.environment()
    }

  def printRunMode: IO[Unit] = {
    sys.props.getOrElse("run.mode", "dev") match {
      case "dev" => Logger[IO].info("Running in development mode.")
      case "prod"  =>Logger[IO].info("Running in production mode.")
    }
  }
}
