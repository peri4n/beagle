package io.beagle

import cats.effect.IO
import io.beagle.components.{Env, Services}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.global

object App {

  private val Logger = LoggerFactory.getLogger(classOf[App])

  def main(args: Array[String]): Unit = {
    Logger.info("Welcome to bIO - the search engine for biological sequences.")

    implicit val cs = IO.contextShift(global)
    implicit val timer = IO.timer(global)

    val environment = Env.autoDetect
    Logger.info(environment.toString)

    val preconditions = for {
      _ <- Services.elasticSearch.run(environment).connectionCheck()
      _ <- Services.elasticSearch.run(environment).createSequenceIndex()
    } yield ()

    // Needed by `BlazeServerBuilder`. Provided by `IOApp`.
    val server = BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(environment.controllers.all.orNotFound)
      .resource

    val program = for {
      _ <- preconditions
      code <- server.use(_ => IO.never).start
    } yield code

    program.unsafeRunSync()
  }

}

