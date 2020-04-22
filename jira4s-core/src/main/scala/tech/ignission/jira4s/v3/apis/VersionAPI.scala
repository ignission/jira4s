package tech.ignission.jira4s.v3.apis

import tech.ignission.jira4s.v3.Credentials
import tech.ignission.jira4s.v3.datas.{IdOrKeyParam, Project, Version}
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl.Response
import tech.ignission.jira4s.v3.dsl.{HttpDSL, HttpQuery}
import tech.ignission.jira4s.v3.datas.IdParam

class VersionAPI[F[_]](baseUrl: String, credentials: Credentials)(implicit httpDSL: HttpDSL[F]) {
  import tech.ignission.jira4s.v3.formatters.SprayJsonFormats._

  private val resource = s"/rest/api/3"

  def all(idOrKeyParam: IdOrKeyParam[Project]): F[Response[Seq[Version]]] =
    httpDSL.get[Seq[Version]](
      HttpQuery(
        path = s"$resource/project/$idOrKeyParam/versions",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def get(id: IdParam[Version]): F[Response[Version]] =
    httpDSL.get[Version](
      HttpQuery(
        path = s"$resource/version/${id.toString}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
