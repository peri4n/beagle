package io.beagle.repository.assoc

import doobie.free.connection.ConnectionIO
import io.beagle.domain.assoc.ProjectDatasetRel

trait ProjectDatasetRelRepo {

  def create(rel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel]

  def update(oldRel: ProjectDatasetRel, newRel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel]

  def delete(rel: ProjectDatasetRel): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}
