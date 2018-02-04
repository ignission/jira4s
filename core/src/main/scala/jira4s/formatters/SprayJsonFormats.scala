package jira4s.formatters

import jira4s.datas.User
import spray.json._

object SprayJsonFormats extends DefaultJsonProtocol {

  // User
  implicit val userFormat: JsonFormat[User] = jsonFormat4(User)

}
