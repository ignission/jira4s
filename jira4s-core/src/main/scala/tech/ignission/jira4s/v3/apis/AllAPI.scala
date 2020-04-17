package tech.ignission.jira4s.v3.apis

import tech.ignission.jira4s.v3.Credentials
import tech.ignission.jira4s.v3.dsl.HttpDSL

class AllAPI[F[_]](baseUrl: String, credentials: Credentials)(implicit httpDSL: HttpDSL[F]) {

  lazy val userAPI = new UserAPI(baseUrl, credentials)
  lazy val versionAPI = new VersionAPI(baseUrl, credentials)

}
