package io.beagle.repository

import cats.effect.IO
import doobie.implicits._
import io.beagle.components.DatabaseSettings
import io.beagle.domain.{Alphabet, SequenceSet}

class SequenceSetDBRepo(settings: DatabaseSettings) extends SequenceSetRepo {

  def create(name: String, alphabet: Alphabet): IO[SequenceSet] = {
    sql"insert into sequence_set (name, alphabet) values ($name, ${ alphabet.toString })".update
      .withUniqueGeneratedKeys[SequenceSet]("id", "name", "alphabet")
      .transact(settings.transactor)
  }

  def update(name: String, alphabet: Alphabet): IO[SequenceSet] = ???

  def find(name: String): IO[Option[SequenceSet]] = ???

  def delete(name: String): IO[Unit] = ???
}
