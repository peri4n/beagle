package io.beagle.persistence.repository.assoc

import doobie.free.connection.ConnectionIO
import doobie.implicits._

case object DbProjectDatasetRelRepo {

  def create(rel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel] =
    sql"""INSERT INTO datasets_projects (dataset_id, project_id)
          VALUES (${ rel.datasetId }, ${ rel.projectId })"""
      .update
      .withUniqueGeneratedKeys[ProjectDatasetRel]("dataset_id", "project_id")

  def update(oldRel: ProjectDatasetRel, newRel: ProjectDatasetRel): ConnectionIO[ProjectDatasetRel] =
    sql"""UPDATE datasets_projects SET project_id = ${ newRel.projectId }, dataset_id = ${ newRel.datasetId }
        WHERE project_id = ${ oldRel.projectId } AND dataset_id = ${ oldRel.datasetId }"""
      .update
      .withUniqueGeneratedKeys[ProjectDatasetRel]("dataset_id", "project_id")

  def delete(rel: ProjectDatasetRel): ConnectionIO[Unit] =
    sql"DELETE FROM datasets_projects WHERE project_id = ${ rel.projectId } AND dataset_id = ${ rel.datasetId }"
      .update
      .run
      .map(_ => ())

  def deleteAll(): ConnectionIO[Unit] =
    sql"DELETE FROM datasets_projects".update
      .run
      .map(_ => ())
}
