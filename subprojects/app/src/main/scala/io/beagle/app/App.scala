package io.beagle.app

import cats.effect.{IO, Sync}
import com.zaxxer.hikari.HikariDataSource
import doobie.free.connection.ConnectionIO
import io.beagle.components.Web
import io.beagle.domain.{User, UserItem}
import io.beagle.persistence.service.UserService
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import io.beagle.persistence.repository.user.UserRepo

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences")

    loadWebserver match {
      case Left(value) => Logger.info(value.toString)
      case Right(server) => {
        Logger.info("Done loading the environment")

        implicit val cs = server.execution.threadPool
        import server.persistence._

        createDb.unsafeRunSync()

        val xa = transactor
        val searchService = server.searchService

        val createSearchIndex = for {
          _ <- searchService.connectionCheck()
          _ <- searchService.createSequenceIndex()
        } yield ()

        val setup = for {
          _ <- createTables
          _ <- injectAdmin(xa)
          _ <- createSearchIndex
        } yield ()

        setup.unsafeRunAsyncAndForget()

        Logger.info("Starting webserver")
        val program = for {
          _ <- server.server.use(_ => IO.never).start
        } yield ()

        program.unsafeRunSync()
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
