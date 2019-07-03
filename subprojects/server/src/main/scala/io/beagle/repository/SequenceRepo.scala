package io.beagle.repository

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.{DatabaseSettings, Settings}
import io.beagle.domain.Sequence

object SequenceRepo {
  val instance = Settings.database map { SequenceRepo(_) }
}

case class SequenceRepo(settings: DatabaseSettings) {
  def create(identifier: String, sequence: String): IO[Sequence] = {
    sql"insert into sequence (identifier, sequence) values ($identifier, $sequence)".update
      .withUniqueGeneratedKeys[Sequence]("id", "identifier", "sequence")
      .transact(settings.transactor)
  }

}
