package io.beagle.domain

import java.time.LocalDateTime

case class ProjectId(value: Long) extends AnyVal

case class Project(name: String,
                   ownerId: UserId,
                   created: LocalDateTime = LocalDateTime.now())

case class ProjectItem(id: ProjectId, project: Project)

