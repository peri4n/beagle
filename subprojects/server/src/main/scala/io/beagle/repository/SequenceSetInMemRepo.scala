package io.beagle.repository

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Alphabet, SequenceSet}

case class SequenceSetInMemRepo(db: Ref[IO, Map[Int, SequenceSet]], counter: Ref[IO, Int]) extends SequenceSetRepo {
  def create(name: String, alphabet: Alphabet): IO[SequenceSet] = {
    SequenceSet(1, name)
    for {
      _ <- counter.update(_ + 1)
      id <- counter.get
      set = SequenceSet(id, name)
      _ <- db.update(store => store + ( id -> set ))
    } yield set
  }

  def update(name: String, alphabet: Alphabet): IO[SequenceSet] = ???

  def find(name: String): IO[Option[SequenceSet]] = ???

  def delete(name: String): IO[Unit] = ???
}
