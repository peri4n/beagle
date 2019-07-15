package io.beagle.domain

import java.time.Instant
import java.util.Date

import cats.effect.IO
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.circe._

final case class SeqSetId(value: Long) extends AnyVal

object SeqSetId {
  implicit val entityDecoder = jsonOf[IO, SeqSetId]
  implicit val entityEncoder = jsonEncoderOf[IO, SeqSetId]
}

case class SeqSet(
                   name: String,
                   created: Date = Date.from(Instant.now()),
                   lastModified: Date = Date.from(Instant.now)
                 )

case class SeqSetView(
                   id: SeqSetId,
                   set: SeqSet
                 )

object SeqSetView {

  implicit val DateFormat: Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
    override def apply(a: Date): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Date] = Decoder.decodeLong.map(s => Date.from(Instant.ofEpochSecond(s))).apply(c)
  }
  implicit val entityDecoder = jsonOf[IO, SeqSetView]
  implicit val entityEncoder = jsonEncoderOf[IO, SeqSetView]
}
