package io.beagle.domain

import java.time.ZonedDateTime

case class DatasetId(value: Long) extends AnyVal

case class Dataset(name: String,
                   ownerId: UserId,
                   projectId: ProjectId,
                   created: ZonedDateTime = ZonedDateTime.now(),
                   lastModified: ZonedDateTime = ZonedDateTime.now())

case class DatasetItem(id: DatasetId, dataset: Dataset)
