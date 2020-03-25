package io.beagle.persistence.repository.assoc

import doobie.free.connection.ConnectionIO

trait ProjectDatasetRelRepo {

  def create(rel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel]

  def update(oldRel: ProjectDatasetRel, newRel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel]

  def delete(rel: ProjectDatasetRel): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]
}
