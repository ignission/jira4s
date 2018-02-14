package jira4s.apis

import jira4s.datas.Credentials

class AllApi(override val baseUrl: String,
             override val credentials: Credentials) extends Api {

  lazy val userApi = UserApi(baseUrl, credentials)

}

object AllApi extends ApiContext[AllApi] {
  override def apply(baseUrl: String, credentials: Credentials): AllApi =
    new AllApi(baseUrl, credentials)
}