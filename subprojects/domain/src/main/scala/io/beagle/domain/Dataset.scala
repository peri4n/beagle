package io.beagle.domain

import java.time.LocalDateTime

case class DatasetId(value: Long) extends AnyVal

case class Dataset(name: String,
                   ownerId: UserId,
                   projectIds: ProjectId,
                   created: LocalDateTime = LocalDateTime.now(),
                   lastModified: LocalDateTime = LocalDateTime.now())

case class DatasetItem(id: DatasetId, dataset: Dataset)
