package tech.ignission.jira4s.v3.datas

case class User(
  accountId: String,
  emailAddress: Option[String],
  displayName: String,
  active: Boolean,
)
