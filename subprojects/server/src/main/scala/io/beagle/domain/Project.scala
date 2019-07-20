package io.beagle.domain


case class ProjectId(value: Long) extends AnyVal

case class Project(name: String)

case class ProjectItem(id: ProjectId, project: Project)
