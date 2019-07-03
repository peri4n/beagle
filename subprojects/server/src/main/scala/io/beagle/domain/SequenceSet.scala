package io.beagle.domain

import java.time.Instant
import java.util.Date

import cats.effect.IO
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

case class SequenceSet(
                        id: Int,
                        name: String,
                        created: Date,
                        lastModified: Date
                      )

object SequenceSet {

  implicit val DateFormat : Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
    override def apply(a: Date): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Date] = Decoder.decodeLong.map(s => Date.from(Instant.ofEpochSecond(s))).apply(c)
  }
  implicit val entityDecoder: EntityDecoder[IO, SequenceSet] = jsonOf[IO, SequenceSet]
  implicit val entityEncoder: EntityEncoder[IO, SequenceSet] = jsonEncoderOf[IO, SequenceSet]
}
