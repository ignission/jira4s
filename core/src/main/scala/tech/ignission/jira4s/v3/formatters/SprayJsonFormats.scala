package tech.ignission.jira4s.v3.formatters

import spray.json._
import tech.ignission.jira4s.v3.ApiErrors
import tech.ignission.jira4s.v3.datas._

object SprayJsonFormats extends DefaultJsonProtocol {

  // Foundation types
  implicit object CalendarDateFormat extends RootJsonFormat[CalendarDate] {
    def write(c: CalendarDate) =
      JsString("")

    def read(value: JsValue) =
      value match {
        case JsString(_) => CalendarDate()
        case _ => deserializationError("String expected")
      }
  }

  // Version
  implicit val versionFormat: JsonFormat[Version] = jsonFormat6(Version)

  // User
  implicit val userFormat: JsonFormat[User] = jsonFormat4(User)

  // Errors
  implicit val apiErrorsFormat: JsonFormat[ApiErrors] = jsonFormat2(ApiErrors)

}
