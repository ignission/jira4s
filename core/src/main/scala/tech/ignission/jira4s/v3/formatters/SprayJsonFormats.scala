package tech.ignission.jira4s.v3.formatters

import spray.json._
import tech.ignission.jira4s.v3.ApiErrors
import tech.ignission.jira4s.v3.datas.User

object SprayJsonFormats extends DefaultJsonProtocol {

  // User
  implicit val userFormat: JsonFormat[User] = jsonFormat5(User)

  // Errors
  implicit val apiErrorsFormat: JsonFormat[ApiErrors] = jsonFormat2(ApiErrors)

}
