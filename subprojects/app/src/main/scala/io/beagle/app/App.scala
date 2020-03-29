package io.beagle.app

import cats.effect.IO
import io.beagle.components.Web
import io.beagle.domain.User
import io.beagle.persistence.service.UserService
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import doobie.implicits._

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences")

    loadWebserver match {
      case Left(value) => Logger.info(value.toString)
      case Right(server) => {
        Logger.info("Done loading the environment")

        implicit val cs = server.execution.threadPool

        val xa = server.persistence.transactor
        val searchService = server.searchService

        UserService.create(User("admin", "admin", "admin@beagle.io"))
          .transact(xa)
          .unsafeRunSync()

        val createSearchIndex = for {
          _ <- searchService.connectionCheck()
          _ <- searchService.createSequenceIndex()
        } yield ()

        createSearchIndex.unsafeRunSync()

        Logger.info("Starting webserver")
        val program = for {
          _ <- server.server.use(_ => IO.never).start
        } yield ()

        program.unsafeRunSync()
      }
    }
  }

  def loadWebserver = ConfigSource.defaultApplication.load[Web]
}
