package io.beagle.service

import doobie.free.connection.ConnectionIO
import io.beagle.domain.assoc.ProjectDatasetRel
import io.beagle.domain.{Dataset, Project}
import io.beagle.repository.assoc.ProjectDatasetRelRepo

case class ProjectDatasetRelService(projectService: ProjectService, datasetService: DatasetService, relRepo: ProjectDatasetRelRepo) {

  def addDatasetToProject(project: Project, dataset: Dataset): ConnectionIO[ProjectDatasetRel] = {
    ???
  }

  def removeDatasetFromProject(project: Project, dataset: Dataset): ConnectionIO[ProjectDatasetRel] = {
    ???
  }
}
