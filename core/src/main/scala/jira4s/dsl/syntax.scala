package jira4s.dsl

import jira4s.dsl.ApiDSL.ApiProgram
import jira4s.exceptions.JiraApiException

object syntax {
  import jira4s.dsl.HttpDSL._

  implicit class ResponseOps[A](response: Response[A]) {
    def orFail: ApiProgram[A] =
      response match {
        case Right(value) => pure(value.asInstanceOf[A])
        case Left(error) => throw JiraApiException(error)
      }
  }

  implicit class ApiOps[A](apiPrg: ApiProgram[Response[A]]) {
    def orFail: ApiProgram[A] =
      apiPrg.flatMap {
        case Right(value) => pure(value)
        case Left(error) => throw JiraApiException(error)
      }
  }
}
