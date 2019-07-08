import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Server {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("elevio")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val client = new ElevioAPIClientImpl()
    val service = new ArticleService(client)
    val api = new ArticleAPI(service)
    val bindingFuture = Http().bindAndHandle(api.route(), "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }
}
