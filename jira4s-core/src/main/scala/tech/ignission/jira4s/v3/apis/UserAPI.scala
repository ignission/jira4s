package tech.ignission.jira4s.v3.apis

import tech.ignission.jira4s.v3.Credentials
import tech.ignission.jira4s.v3.datas.User
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl.Response
import tech.ignission.jira4s.v3.dsl.{HttpDSL, HttpQuery}

class UserAPI[F[_]](baseUrl: String, credentials: Credentials)(implicit httpDSL: HttpDSL[F]) {
  import tech.ignission.jira4s.v3.formatters.SprayJsonFormats._

  private val resource = s"/rest/api/3/users"

  lazy val all: F[Response[Seq[User]]] =
    httpDSL.get[Seq[User]](
      HttpQuery(
        path = s"$resource/search",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
