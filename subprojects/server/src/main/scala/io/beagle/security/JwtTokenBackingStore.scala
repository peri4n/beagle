package io.beagle.security

import cats.data.OptionT
import cats.effect.IO
import cats.effect.concurrent.Ref
import tsec.authentication.{AugmentedJWT, BackingStore}
import tsec.common.SecureRandomId
import tsec.mac.jca.HMACSHA256

case class JwtTokenBackingStore(db: Ref[IO, Map[SecureRandomId, AugmentedJWT[HMACSHA256, Int]]]) extends BackingStore[IO, SecureRandomId, AugmentedJWT[HMACSHA256, Int]] {

  def put(elem: AugmentedJWT[HMACSHA256, Int]): IO[AugmentedJWT[HMACSHA256, Int]] =
    db.modify(db => (db.updated(elem.id, elem), elem))

  def update(v: AugmentedJWT[HMACSHA256, Int]): IO[AugmentedJWT[HMACSHA256, Int]] =
    db.modify(db => (db.updated(v.id, v), v))

  def delete(id: SecureRandomId): IO[Unit] =
    db.update(db => db - id)

  def get(id: SecureRandomId): OptionT[IO, AugmentedJWT[HMACSHA256, Int]] =
    OptionT(db.get.map(_.get(id)))
}

object JwtTokenBackingStore {

  def inMemory =
    JwtTokenBackingStore(
      Ref.unsafe[IO, Map[SecureRandomId, AugmentedJWT[HMACSHA256, Int]]](Map.empty)
    )
}
