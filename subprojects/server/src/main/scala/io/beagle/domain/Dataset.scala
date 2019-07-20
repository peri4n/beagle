package io.beagle.domain

import java.time.Instant
import java.util.Date

import io.circe.Decoder.Result
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, HCursor, Json}

case class DatasetId(value: Long) extends AnyVal

object DatasetId {
  implicit val fooDecoder: Decoder[DatasetId] = deriveDecoder[DatasetId]
  implicit val fooEncoder: Encoder[DatasetId] = deriveEncoder[DatasetId]
}

case class Dataset(name: String, created: Date = Date.from(Instant.now()), lastModified: Date = Date.from(Instant.now))

object Dataset {
  implicit val fooDecoder: Decoder[Dataset] = deriveDecoder[Dataset]
  implicit val fooEncoder: Encoder[Dataset] = deriveEncoder[Dataset]
  implicit val DateFormat: Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
    override def apply(a: Date): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Date] = Decoder.decodeLong.map(s => Date.from(Instant.ofEpochSecond(s))).apply(c)
  }
}

case class DatasetItem(id: DatasetId, set: Dataset)

object DatasetItem {
  implicit val fooDecoder: Decoder[DatasetItem] = deriveDecoder[DatasetItem]
  implicit val fooEncoder: Encoder[DatasetItem] = deriveEncoder[DatasetItem]
}
