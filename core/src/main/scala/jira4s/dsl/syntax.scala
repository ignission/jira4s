package jira4s.dsl

import jira4s.dsl.ApiDsl.ApiPrg
import jira4s.dsl.HttpADT.Response
import jira4s.exceptions.JiraApiException

object syntax {

  import ApiDsl.HttpOp._

  implicit class ResponseOps[A](response: Response[A]) {
    def orFail: ApiPrg[A] =
      response match {
        case Right(value) => pure(value.asInstanceOf[A])
        case Left(error) => throw JiraApiException(error)
      }
  }

  implicit class ApiOps[A](apiPrg: ApiPrg[Response[A]]) {
    def orFail: ApiPrg[A] =
      apiPrg.flatMap {
        case Right(value) => pure(value)
        case Left(error) => throw JiraApiException(error)
      }
  }
}
