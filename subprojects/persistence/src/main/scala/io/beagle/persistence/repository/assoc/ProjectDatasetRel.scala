package io.beagle.persistence.repository.assoc

import io.beagle.domain.{DatasetId, ProjectId}

case class ProjectDatasetRel(projectId: ProjectId, datasetId: DatasetId)
