package io.beagle

import cats.effect.{ContextShift, IO, Timer}
import io.beagle.components.{Env, Services}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    val environment = Env.autoDetect
    Logger.info(environment.toString)

    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)

    val preconditions = for {
      _ <- Services.elasticSearch(environment).connectionCheck()
      _ <- Services.elasticSearch(environment).createSequenceIndex()
    } yield ()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(environment.controllers.all.orNotFound)
      .resource

    val program = for {
      _ <- preconditions
      _ <- server.use(_ => IO.never).start
    } yield ()

    program.unsafeRunSync()
  }

}

