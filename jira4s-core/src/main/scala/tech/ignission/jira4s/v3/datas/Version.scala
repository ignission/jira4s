package tech.ignission.jira4s.v3.datas

case class Version(
  id: Id[Version],
  name: String,
  description: Option[String],
  archived: Boolean,
  released: Boolean,
  releaseDate: Option[CalendarDate] // TODO: implement
)
