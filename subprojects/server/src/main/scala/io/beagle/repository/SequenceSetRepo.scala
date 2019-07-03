package io.beagle.repository

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.{DatabaseSettings, Settings}
import io.beagle.domain.{Alphabet, SequenceSet}

object SequenceSetRepo {
  val instance = Settings.database map { SequenceSetRepo(_) }
}

case class SequenceSetRepo(settings: DatabaseSettings) {
  def create(name: String, alphabet: Alphabet): IO[SequenceSet] = {
    sql"insert into sequence_set (name, alphabet) values ($name, ${ alphabet.toString })".update
      .withUniqueGeneratedKeys[SequenceSet]("id", "name", "alphabet")
      .transact(settings.transactor)
  }

}
