package tech.ignission.jira4s.v3.datas

case class Version(
  id: Double,
  name: String,
  description: String,
  archived: Boolean,
  released: Boolean,
  releaseDate: CalendarDate
)
