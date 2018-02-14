import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.implicits._
import jira4s.apis.AllApi
import jira4s.datas.{Key, User}
import jira4s.interpreters.AkkaHttpInterpret

import scala.util.{Failure, Success}

object Main extends App {

  import jira4s.dsl.syntax._

  implicit val system = ActorSystem("test")
  implicit val mat = ActorMaterializer()
  implicit val exc = system.dispatcher

  val httpInterpret = new AkkaHttpInterpret()
  val interpreter = httpInterpret

  val baseUrl = "https://xxxxxxxxxx.atlassian.net/rest/api/2"
  val username = "username"
  val password = "password"

  val allApi = AllApi.basic(baseUrl, username, password)

  val prg = allApi.userApi.byKey(Key[User]("user1")).orFail

  prg.foldMap(interpreter).onComplete {
    case Success(data) => println(data)
    case Failure(ex) => ex.printStackTrace()
  }
  system.terminate()
}
