package jira4s.interpreters

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import cats.Monad
import cats.effect.IO
import cats.implicits._
import fs2.interop.reactivestreams._
import jira4s.datas.{ApiErrors, Basic}
import jira4s.dsl.HttpADT.{ByteStream, Bytes, Response}
import jira4s.dsl.{HttpQuery, JiraHttpInterpret, RequestError, ServerDown}
import jira4s.dsl.JiraHttpOp.HttpF
import spray.json._

import scala.collection.immutable.Seq
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

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest =
    query.credentials match {
      case Basic(username, password) =>
        HttpRequest(
          method = method,
          uri = Uri(query.baseUrl + query.path).withQuery(Query(query.params))
        ).withHeaders(
          reqHeaders :+
            headers.Authorization(BasicHttpCredentials(username, password))
        )
    }

  private def createRequest[Payload](method: HttpMethod,
                                     query: HttpQuery,
                                     payload: Payload,
                                     format: JsonFormat[Payload]): Future[HttpRequest] = {
    val formData = FormData(
      payload.toJson(format).asJsObject.fields.map {
        case (key, JsString(value)) => key -> value
        case (key, value) => key -> value.toString()
      }
    )

    Marshal(formData).to[RequestEntity].map { entity =>
      createRequest(method, query).withEntity(entity)
    }
  }

  private def doRequest(request: HttpRequest): Future[Response[Bytes]] =
    for {
      response <- http.singleRequest(request)
      data <- response.entity.toStrict(timeout).map(_.data.utf8String)
      result = {
        val status = response.status.intValue()
        if (response.status.isFailure()) {
          if (status >= 400 && status < 500)
            Either.left(RequestError(data.parseJson.convertTo[ApiErrors]))
          else {
            Either.left(ServerDown)
          }
        } else {
          Either.right(data)
        }
      }
    } yield result

  override def get[A](query: HttpQuery, format: JsonFormat[A]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.GET, query))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    for {
      request <- createRequest(HttpMethods.POST, query, payload, payloadFormat)
      serverResponse <- doRequest(request)
      response = serverResponse
        .map { content =>
          if (content.isEmpty) "{}" else content
        }
        .map(_.parseJson.convertTo[A](format))
    } yield response

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    for {
      request <- createRequest(HttpMethods.PUT, query, payload, payloadFormat)
      serverResponse <- doRequest(request)
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def delete(query: HttpQuery): Future[Response[Unit]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.DELETE, query))
      response = serverResponse.map(_ => ())
    } yield response

  override def download(query: HttpQuery): Future[Response[ByteStream]] = {
    val request = createRequest(HttpMethods.GET, query)
    for {
      serverResponse <- followRedirect(request)
      response <- {
        val status = serverResponse.status.intValue()
        if (serverResponse.status.isFailure()) {
          if (status >= 400 && status < 500)
            serverResponse
              .entity.toStrict(timeout)
              .map(_.data.utf8String)
              .map(data => Either.left(RequestError(data.parseJson.convertTo[ApiErrors])))
          else {
            serverResponse.entity.discardBytes()
            Future.successful(Either.left(ServerDown))
          }
        } else {
          val stream = serverResponse.entity.dataBytes
            .map(_.asByteBuffer)
            .runWith(Sink.asPublisher(true))
            .toStream[IO]
          Future.successful(Either.right(stream))
        }
      }
    } yield response
  }

  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat [A]): Future[Response[A]] = {
    val formData = Multipart.FormData(
      Source.single(
        Multipart.FormData.BodyPart(
          file.getName,
          HttpEntity(MediaTypes.`application/octet-stream`, file.length(), FileIO.fromPath(file.toPath)),
          Map(
            "name" -> "file",
            "filename" -> file.getName
          )
        )
      )
    )

    for {
      entity <- Marshal(formData).to[RequestEntity]
      request = createRequest(HttpMethods.POST, query).withEntity(entity)
      serverResponse <- doRequest(request)
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response
  }

  override def pure[A](a: A): Future[A] =
    Future.successful(a)

  override def parallel[A](prgs: scala.Seq[HttpF[A]]): Future[scala.Seq[A]] =
    Future.sequence(
      prgs.map(_.foldMap(this))
    ).map { result =>
      result
    }

  private def followRedirect(req: HttpRequest, count: Int = 0): Future[HttpResponse] = {
    http.singleRequest(req).flatMap { resp =>
      resp.status match {
        case StatusCodes.Found | StatusCodes.SeeOther => resp.header[headers.Location].map { loc =>
          resp.entity.discardBytes()
          val locUri = loc.uri
          val newUri = locUri
          val newReq = req.copy(
            uri = newUri,
            headers = reqHeaders
          )
          if (count < maxRedirCount) followRedirect(newReq, count + 1) else Http().singleRequest(newReq)
        }.getOrElse(throw new RuntimeException(s"location not found on 302 for ${req.uri}"))
        case _ => Future(resp)
      }
    }
  }
}
