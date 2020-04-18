package tech.ignission.jira4s.test.v3

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import tech.ignission.jira4s.interpreters.JiraHttpDSLOnAkka
import tech.ignission.jira4s.v3.Basic
import tech.ignission.jira4s.v3.apis.AllAPI
import tech.ignission.jira4s.v3.datas.{IdOrKeyParam, IdParam, Key, Project, Version}
import tech.ignission.jira4s.v3.dsl.syntax._

import scala.util.{Failure, Success}

object App {

  implicit val system    = ActorSystem("test")
  implicit val mat       = SystemMaterializer(system)
  implicit val scheduler = monix.execution.Scheduler.Implicits.global

  val akkaHttpDSL = new JiraHttpDSLOnAkka()

  def main(args: Array[String]): Unit = {
    if (args.length > 2) {
      val apiUrl = args.apply(0)
      val apiUser = args.apply(1)
      val apiPass = args.apply(2)
      val credential = Basic(apiUser, apiPass)
      val allAPI = new AllAPI(apiUrl, credential)(akkaHttpDSL)

      val task = for {
        users <- allAPI.userAPI.all.handleError
        versions <- allAPI.versionAPI.all(IdOrKeyParam.projectKey(Key[Project]("TEST"))).handleError
        version <- allAPI.versionAPI.get(IdParam.versionId(versions.head.id)).handleError
      } yield (users, versions, version)

      task.value.runToFuture.onComplete {
        case Success(result) =>
          result match {
            case Right((users, versions, version)) =>
              println(users)
              println(versions)
              println(version)
            case Left(error) =>
              println(error)
          }
          akkaHttpDSL.terminate().runSyncUnsafe()
          system.terminate()
        case Failure(ex) =>
          ex.printStackTrace()
      }
    } else {
      println("Missing argument api url, username and password")
    }
  }
}
