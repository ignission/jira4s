package jira4s.dsl

import jira4s.datas.Credentials

case class HttpQuery(
  path: String,
  params: Map[String, String] = Map(),
  credentials: Credentials,
  baseUrl: String
)
