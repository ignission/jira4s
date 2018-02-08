package jira4s.datas

sealed trait Credentials
case class Basic(username: String, password: String) extends Credentials
