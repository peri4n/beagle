package io.beagle.domain.assoc

import io.beagle.domain.{DatasetId, ProjectId}

case class ProjectDatasetRel(projectId: ProjectId, datasetId: DatasetId)
