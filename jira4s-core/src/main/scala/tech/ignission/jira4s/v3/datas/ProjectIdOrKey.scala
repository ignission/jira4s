package tech.ignission.jira4s.v3.datas

sealed trait ProjectIdOrKey

case class ProjectId(value: String) extends ProjectIdOrKey
case class ProjectKey(value: String) extends ProjectIdOrKey
