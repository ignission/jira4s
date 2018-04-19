package jira4s.dsl

import java.io.File

import cats.free.Free
import jira4s.dsl.HttpDSL.{ByteStream, Response}
import spray.json.JsonFormat

sealed trait HttpADT[A]

private[jira4s] case class Get[A](
  query: HttpQuery,
  format: JsonFormat[A]
) extends HttpADT[Response[A]]

private[jira4s] case class Post[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
) extends HttpADT[Response[A]]

private[jira4s] case class Put[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
) extends HttpADT[Response[A]]

private[jira4s] case class Delete(
  query: HttpQuery
) extends HttpADT[Response[Unit]]

private[jira4s] case class Download(
  query: HttpQuery
) extends HttpADT[Response[ByteStream]]

private[jira4s] case class Upload[A](
  query: HttpQuery,
  file: File,
  format: JsonFormat[A]
) extends HttpADT[Response[A]]

private[jira4s] case class Pure[A](a: A) extends HttpADT[A]

private[jira4s] case class Suspend[A](a: () => Free[HttpADT, A]) extends HttpADT[A]

private[jira4s] case class Parallel[A](prgs: Seq[Free[HttpADT, A]]) extends HttpADT[Seq[A]]
