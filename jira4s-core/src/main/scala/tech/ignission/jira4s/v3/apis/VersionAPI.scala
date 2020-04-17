package tech.ignission.jira4s.v3.apis

import tech.ignission.jira4s.v3.Credentials
import tech.ignission.jira4s.v3.datas.{IdOrKeyParam, Project, Version}
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl.Response
import tech.ignission.jira4s.v3.dsl.{HttpDSL, HttpQuery}

class VersionAPI[F[_]](baseUrl: String, credentials: Credentials)(implicit httpDSL: HttpDSL[F]) {
  import tech.ignission.jira4s.v3.formatters.SprayJsonFormats._

  private val resource = s"/rest/api/3/project"

  def all(idOrKeyParam: IdOrKeyParam[Project]): F[Response[Seq[Version]]] =
    httpDSL.get[Seq[Version]](
      HttpQuery(
        path = s"$resource/$idOrKeyParam/versions",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
