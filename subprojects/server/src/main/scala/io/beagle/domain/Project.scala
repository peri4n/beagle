package io.beagle.domain

import java.time.OffsetDateTime

import doobie.util.Meta


case class ProjectId(value: Long) extends AnyVal

case class Project(name: String,
                   createdBy: OffsetDateTime = OffsetDateTime.now(),
                   lastModified: OffsetDateTime = OffsetDateTime.now())

object ProjectItem {
  implicit val date2String: Meta[OffsetDateTime] = Meta[String].timap(OffsetDateTime.parse)(_.toString)

}

case class ProjectItem(id: ProjectId, project: Project)
