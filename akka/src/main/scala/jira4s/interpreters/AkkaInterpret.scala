package jira4s.interpreters

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import cats.Monad
import jira4s.dsl.HttpADT.{ByteStream, Response}
import jira4s.dsl.{HttpQuery, JiraHttpInterpret}
import jira4s.dsl.JiraHttpOp.HttpF
import spray.json.JsonFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class AkkaInterpret(implicit actorSystem: ActorSystem,
                    mat: Materializer,
                    exc: ExecutionContext) extends JiraHttpInterpret[Future] {

  import jira4s.formatters.SprayJsonFormats._

  implicit val monad = implicitly[Monad[Future]]

  private val http = Http()
  private val timeout = 10.seconds
  private val maxRedirCount = 20
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("jira4s"),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  override def get[A](query: HttpQuery, format: JsonFormat[A]): Future[Response[A]] = ???

  override def create[Payload, A](query: HttpQuery, payload: Payload, format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): Future[Response[A]] = ???

  override def update[Payload, A](query: HttpQuery, payload: Payload, format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): Future[Response[A]] = ???

  override def delete(query: HttpQuery): Future[Response[Unit]] = ???

  override def download(query: HttpQuery): Future[Response[ByteStream]] = ???

  override def upload[A](query: HttpQuery, file: File, format: JsonFormat [A]): Future[Response[A]] = ???

  override def pure[A](a: A): Future[A] = ???

  override def parallel[A](prgs: Seq[HttpF[A]]): Future[Seq[A]] = ???
}
