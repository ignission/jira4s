package jira4s.formatters

import jira4s.datas.{ApiErrors, User}
import spray.json._

object SprayJsonFormats extends DefaultJsonProtocol {

  // User
  implicit val userFormat: JsonFormat[User] = jsonFormat4(User)

  // Errors
  implicit val apiErrorsFormat: JsonFormat[ApiErrors] = jsonFormat2(ApiErrors)

}
