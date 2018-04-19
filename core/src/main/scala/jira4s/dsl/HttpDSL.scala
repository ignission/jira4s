package jira4s.dsl

import java.io.File
import java.nio.ByteBuffer

import cats.effect.IO
import cats.free.Free
import fs2.Stream
import spray.json.JsonFormat

object HttpDSL {
  type Bytes = String
  type Response[A] = Either[HttpError, A]
  type ByteStream = Stream[IO, ByteBuffer]

  type HttpProgram[A] = Free[HttpADT, A]

  def pure[A](a: A): HttpProgram[A] =
    Free.liftF(Pure(a))

  def suspend[A](a: => HttpProgram[A]): HttpProgram[A] =
    Free.liftF(Suspend(() => a))

  def parallel[A](prgs: Seq[HttpProgram[A]]): HttpProgram[Seq[A]] =
    Free.liftF[HttpADT, Seq[A]](Parallel(prgs))

  def post[Payload, A](query: HttpQuery, payload: Payload)
                      (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpProgram[Response[A]] =
    Free.liftF(Post(query, payload, format, payloadFormat))

  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): HttpProgram[Response[A]] =
    Free.liftF[HttpADT, Response[A]](Get(query, format))

  def put[Payload, A](query: HttpQuery, payload: Payload)
                     (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpProgram[Response[A]] =
    Free.liftF(Put(query, payload, format, payloadFormat))

  def delete(query: HttpQuery): HttpProgram[Response[Unit]] =
    Free.liftF(Delete(query))

  def download(query: HttpQuery): HttpProgram[Response[ByteStream]] =
    Free.liftF(Download(query))

  def upload[A](query: HttpQuery, file: File)(implicit format: JsonFormat[A]): HttpProgram[Response[A]] =
    Free.liftF[HttpADT, Response[A]](Upload(query, file, format))
}
