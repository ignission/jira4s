package jira4s.apis

import jira4s.datas.{Credentials, Key, User}
import jira4s.dsl.ApiDsl.ApiPrg
import jira4s.dsl.HttpADT.Response
import jira4s.dsl.HttpQuery

// https://developer.atlassian.com/cloud/jira/platform/rest/#api-api-2-user-get

class UserApi(override val baseUrl: String,
              override val credentials: Credentials) extends Api {

  import jira4s.dsl.ApiDsl.HttpOp._
  import jira4s.formatters.SprayJsonFormats._

  private val resource = "user"

  def byKey(key: Key[User]): ApiPrg[Response[User]] = {
    val query = HttpQuery(
      path = s"/$resource",
      credentials = credentials,
      baseUrl = baseUrl,
      params = Map("key" -> key.value)
    )
    get[User](query)
  }


}

object UserApi extends ApiContext[UserApi] {
  override def apply(baseUrl: String, credentials: Credentials): UserApi =
    new UserApi(baseUrl, credentials)
}