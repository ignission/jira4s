package tech.ignission.jira4s.v3.dsl

import cats.Monad
import cats.data.EitherT
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl.Response

object syntax {

  implicit class ResponseOps[F[_], A](response: F[Response[A]])(implicit M: Monad[F]) {
    def handleError: EitherT[F, HttpError, A] = EitherT(response)
  }

}
