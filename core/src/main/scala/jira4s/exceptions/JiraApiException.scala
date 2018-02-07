package jira4s.exceptions

import jira4s.dsl.HttpError

case class JiraApiException(error: HttpError) extends RuntimeException {
  override def getMessage: String = s"Request failed with error $error"
}
