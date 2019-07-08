import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success}


class ArticleAPI(service: ArticleService)(implicit system: ActorSystem) extends DefaultJsonProtocol with ArticleJsonSupport {

  def route(): Route = {
    path("articles") {
      get {
        onComplete(service.getArticles()) {
          case Success(result) => complete(result)
          case Failure(ex) => complete(StatusCodes.InternalServerError -> s"An error occurred: ${ex.getMessage}")
        }
      }
    }~
    path("article" / Segment) { id =>
      get {
          onComplete(service.getArticle(id)){
            case Success(result) => complete(result)
            case Failure(ex) => complete(StatusCodes.InternalServerError -> s"An error occurred: ${ex.getMessage}")
          }
        }
    }~
    path("search" / Segment) { term =>
      get {
        onComplete(service.searchArticles(term)){
          case Success(result) => complete(result)
          case Failure(ex) => complete(StatusCodes.InternalServerError -> s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }
}
