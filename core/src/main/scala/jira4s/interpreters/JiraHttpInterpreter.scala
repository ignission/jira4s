package jira4s.interpreters

import java.io.File

import cats.{Monad, ~>}
import jira4s.dsl.HttpDSL.{ByteStream, HttpProgram, Response}
import jira4s.dsl._
import spray.json.JsonFormat

trait JiraHttpInterpreter[F[_]] extends (HttpADT ~> F) {

  def get[A](query: HttpQuery, format: JsonFormat[A]): F[Response[A]]

  def create[Payload, A](query: HttpQuery,
                         payload: Payload,
                         format: JsonFormat[A],
                         payloadFormat: JsonFormat[Payload]): F[Response[A]]

  def update[Payload, A](query: HttpQuery,
                         payload: Payload,
                         format: JsonFormat[A],
                         payloadFormat: JsonFormat[Payload]): F[Response[A]]

  def delete(query: HttpQuery): F[Response[Unit]]

  def download(query: HttpQuery): F[Response[ByteStream]]

  def upload[A](query: HttpQuery, file: File, format: JsonFormat[A]): F[Response[A]]

  def pure[A](a: A): F[A]

  def parallel[A](prgs: Seq[HttpProgram[A]]): F[Seq[A]]

  implicit def monad: Monad[F]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Pure(a)            => pure(a)
    case Suspend(prg)       => prg().foldMap(this)
    case Parallel(prgs)     => parallel(prgs)
    case Get(query, format)                           => get(query, format)
    case Put(query, payload, format, payloadFormat)   => update(query, payload, format, payloadFormat)
    case Post(query, payload, format, payloadFormat)  => create(query, payload, format, payloadFormat)
    case Delete(query)                                => delete(query)
    case Download(query)                              => download(query)
    case Upload(query, file, format)                  => upload(query, file, format)
  }
}