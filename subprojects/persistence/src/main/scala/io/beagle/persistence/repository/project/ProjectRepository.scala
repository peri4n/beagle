package io.beagle.persistence.repository.project

import doobie.free.connection.ConnectionIO
import io.beagle.domain.{Project, ProjectId, ProjectItem, UserId}

trait ProjectRepository {
  def createTable(): ConnectionIO[Int]

  def create(project: Project): ConnectionIO[ProjectItem]

  def update(id: ProjectId, project: Project): ConnectionIO[ProjectItem]

  def findById(id: ProjectId): ConnectionIO[Option[ProjectItem]]

  def findByName(name: String, owner: UserId): ConnectionIO[Option[ProjectItem]]

  def delete(id: ProjectId): ConnectionIO[Unit]

  def deleteAll(): ConnectionIO[Unit]

}
