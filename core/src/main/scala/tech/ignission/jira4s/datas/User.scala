package tech.ignission.jira4s.datas

case class User(
  key: Option[Key[User]],
  name: String,
  displayName: String,
  emailAddress: String
)
