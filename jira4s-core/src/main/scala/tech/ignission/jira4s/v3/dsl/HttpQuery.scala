package tech.ignission.jira4s.v3.dsl

import tech.ignission.jira4s.v3.Credentials

case class HttpQuery(
  path: String,
  credentials: Credentials,
  baseUrl: String
)

