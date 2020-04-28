package io.beagle.app

import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.zaxxer.hikari.HikariDataSource
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import io.beagle.components.Web
import io.beagle.domain.User
import io.beagle.persistence.repository.user.UserRepo
import io.beagle.persistence.service.UserService
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object App extends IOApp {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  def run(args: List[String]): IO[ExitCode] = {
    Logger[IO].info("Welcome to bIO - the search engine for biological sequences").unsafeRunSync()

    loadWebserver match {
      case Left(value) => Logger[IO].info(value.toString).map( _ => ExitCode.Error)
      case Right(server) => {

        val searchService = server.searchService
        val esSetup = for {
          _ <- Logger[IO].info("Setting up elastic search environment")
          _ <- searchService.connectionCheck()
          _ <- searchService.createSequenceIndex()
        } yield ()

        val db = server.persistence
        val xa = db.transactor

        val dbSetup = for {
          _ <- Logger[IO].info("Setting up database environment")
          _ <- db.createDb
          _ <- db.createTables
          _ <- injectAdmin(xa)
        } yield ()

        val setup = IO.racePair(esSetup, dbSetup)

        val program = for {
          _ <- setup
          _ <- Logger[IO].info("Starting web server")
          _ <- server.server.use(_ => IO.never).start
        } yield ExitCode.Success

        program
      }
    }
  }

  private def injectAdmin(xa: Aux[IO, HikariDataSource]): IO[Unit] = {
    val admin = User("admin", "admin", "admin@beagle.io")

    val create = for {
      maybeUser <- UserService.findByName(admin.name)
      _ <- maybeUser.fold(UserRepo.create(admin).map(_ => ())) {
        _ => Sync[ConnectionIO].unit
      }
    } yield ()
    create.transact(xa)
  }

  def loadWebserver = ConfigSource.defaultApplication.load[Web]

}
