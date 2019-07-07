package io.beagle.repository

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.beagle.domain.{Alphabet, SequenceSet}

object SequenceSetRepo {

  def inMemory =
  SequenceSetInMemRepo(
    Ref.unsafe[IO, Map[Int, SequenceSet]](Map.empty),
    Ref.unsafe[IO, Int](0)
  )

}

trait SequenceSetRepo {
  def create(name: String, alphabet: Alphabet): IO[SequenceSet]

  def update(name: String, alphabet: Alphabet): IO[SequenceSet]

  def find(name: String): IO[Option[SequenceSet]]

  def delete(name: String): IO[Unit]
}
