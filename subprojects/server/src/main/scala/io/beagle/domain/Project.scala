package io.beagle.domain

import java.time.ZonedDateTime

case class ProjectId(value: Long) extends AnyVal

case class Project(name: String,
                   ownerId: UserId,
                   created: ZonedDateTime = ZonedDateTime.now())

case class ProjectItem(id: ProjectId, project: Project)

