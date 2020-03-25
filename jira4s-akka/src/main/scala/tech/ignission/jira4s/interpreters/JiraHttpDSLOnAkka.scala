package tech.ignission.jira4s.interpreters

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}
import akka.stream.Materializer
import monix.eval.Task
import org.slf4j.LoggerFactory
import spray.json._
import tech.ignission.jira4s.v3.dsl.BacklogHttpDsl.Response
import tech.ignission.jira4s.v3.dsl.{HttpDSL, HttpQuery, RequestError, ServerDown}
import tech.ignission.jira4s.v3.{ApiErrors, Basic}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class JiraHttpDSLOnAkka(optTransport: Option[ClientTransport] = None)
                       (implicit actorSystem: ActorSystem, mat: Materializer, exc: ExecutionContext) extends HttpDSL[Task] {
  import tech.ignission.jira4s.v3.formatters.SprayJsonFormats._

  private val logger = LoggerFactory.getLogger(getClass)

  private val settings = optTransport.map { transport =>
    ConnectionPoolSettings(actorSystem).withConnectionSettings(
      ClientConnectionSettings(actorSystem).withTransport(transport)
    )
  }.getOrElse(ConnectionPoolSettings(actorSystem))

  private val http = Http()
  private val timeout = 10.seconds
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("jira4s"),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  def terminate(): Task[Unit] =
    Task.deferFuture(http.shutdownAllConnectionPools())

  override def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): Task[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.GET, query))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest =
    query.credentials match {
      case Basic(username, password) =>
        val uri = Uri(query.baseUrl + query.path)
        logger.info(s"Create HTTP request method: ${method.value} and uri: $uri")
        HttpRequest(
          method = method,
          uri = uri
        ).withHeaders(reqHeaders :+ createAuthHeader(username = username, password = password))
    }

  private def createAuthHeader(username: String, password: String): HttpHeader =
    headers.Authorization(BasicHttpCredentials(username = username, password = password))

  private def doRequest(request: HttpRequest): Task[Response[String]] = {
    logger.info(s"Execute request $request")
    for {
      response <- Task.deferFuture(http.singleRequest(request, settings = settings))
      data <- Task.deferFuture(response.entity.toStrict(timeout).map(_.data.utf8String))
      result = {
        val status = response.status.intValue()
        logger.info(s"Received response with status: $status")
        if (response.status.isFailure()) {
          if (status >= 400 && status < 500)
            Left(RequestError(data.parseJson.convertTo[ApiErrors]))
          else {
            Left(ServerDown)
          }
        } else {
          logger.info(s"Response data is $data")
          Right(data)
        }
      }
    } yield result
  }
}
