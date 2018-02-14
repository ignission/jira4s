package jira4s.apis

import jira4s.datas.{Basic, Credentials}


trait Api {
  def credentials: Credentials
  def baseUrl: String
}

trait ApiContext[A <: Api] {
  def apply(baseUrl: String, credentials: Credentials): A
  def basic(baseUrl: String, username: String, password: String): A =
    apply(baseUrl, Basic(username, password))
}
