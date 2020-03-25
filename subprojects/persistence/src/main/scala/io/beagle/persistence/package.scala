package io.beagle

import java.sql.Timestamp
import java.time._

import cats.data.State
import doobie.util.Meta

package object persistence {

  object metas {
    implicit val date2String: Meta[OffsetDateTime] = Meta[String].timap(OffsetDateTime.parse)(_.toString)

    implicit val ZonedDateTimeMeta: Meta[ZonedDateTime] =
      Meta[Timestamp].timap(
        ts => ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime), ZoneId.systemDefault))(
        zdt => new Timestamp(Instant.from(zdt).toEpochMilli)
      )
  }

}
