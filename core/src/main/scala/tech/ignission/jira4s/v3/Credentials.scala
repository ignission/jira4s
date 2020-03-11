package tech.ignission.jira4s.v3

sealed trait Credentials
case class Basic(username: String, password: String) extends Credentials
