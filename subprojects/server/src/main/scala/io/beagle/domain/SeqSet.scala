package io.beagle.domain

import java.time.Instant
import java.util.Date

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.semiauto._

case class SeqSetId(value: Long) extends AnyVal
object SeqSetId {
  implicit val fooDecoder: Decoder[SeqSetId] = deriveDecoder[SeqSetId]
  implicit val fooEncoder: Encoder[SeqSetId] = deriveEncoder[SeqSetId]
}

case class SeqSet(name: String, created: Date = Date.from(Instant.now()), lastModified: Date = Date.from(Instant.now))
object SeqSet {
  implicit val fooDecoder: Decoder[SeqSet] = deriveDecoder[SeqSet]
  implicit val fooEncoder: Encoder[SeqSet] = deriveEncoder[SeqSet]
  implicit val DateFormat: Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
    override def apply(a: Date): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Date] = Decoder.decodeLong.map(s => Date.from(Instant.ofEpochSecond(s))).apply(c)
  }
}

case class SeqSetItem(id: SeqSetId, set: SeqSet)
object SeqSetItem {
  implicit val fooDecoder: Decoder[SeqSetItem] = deriveDecoder[SeqSetItem]
  implicit val fooEncoder: Encoder[SeqSetItem] = deriveEncoder[SeqSetItem]
}
