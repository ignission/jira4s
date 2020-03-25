package tech.ignission.jira4s.v3.dsl

import spray.json.JsonFormat
import tech.ignission.jira4s.v3.ApiErrors
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl._

trait HttpDSL[F[_]] {
  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): F[Response[A]]
}

object BacklogHttpDsl {
  type Response[A] = Either[HttpError, A]
}

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case class InvalidResponse(msg: String) extends HttpError
case object ServerDown extends HttpError