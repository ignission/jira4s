package jira4s.formatters

import jira4s.datas.{ApiErrors, Key, User}
import spray.json._

object SprayJsonFormats extends DefaultJsonProtocol {

  class KeyFormat[A]() extends RootJsonFormat[Key[A]] {
    override def read(json: JsValue): Key[A] = json match {
      case JsString(keyVal) => Key(keyVal)
      case _ => throw DeserializationException(s"Expected a js string got ${json.prettyPrint}")
    }
    override def write(obj: Key[A]): JsValue = JsString(obj.value)
  }

  // User
  implicit val KeyUserFormat: JsonFormat[Key[User]] = new KeyFormat[User]
  implicit val userFormat: JsonFormat[User]         = jsonFormat4(User)

  // Errors
  implicit val apiErrorsFormat: JsonFormat[ApiErrors] = jsonFormat2(ApiErrors)

}
