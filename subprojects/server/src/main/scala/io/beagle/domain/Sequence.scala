package io.beagle.domain

import java.util.Date

case class Sequence(
                     id: Int,
                     identifier: String,
                     sequence: String,
                     created: Date,
                     lastModified: Date
                   )
