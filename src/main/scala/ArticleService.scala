import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.Query
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString}

import scala.concurrent.ExecutionContext

case class Article(title: String)

trait ArticleJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val articleFormat = jsonFormat1(Article)

}

class ArticleService(client: ElevioAPIClient)(implicit ec: ExecutionContext, as: ActorSystem) extends ArticleJsonSupport {
  def getArticles(page: Int) = {
    client.runRequest("articles", Seq(Query("page" -> page.toString))).map{ response =>
      response.fields("articles") match {
        case JsArray(items) => items.map {
          case JsObject(obj) => obj("title") match {
            case JsString(value) => Article(value)
          }
        }
      }
    }
  }

  def searchArticles(term: String) = {
    client.runRequest("search/en", Seq(Query("query" -> term))).map{ response =>
      response.fields("results") match {
        case JsArray(items) => items.map {
          case JsObject(obj) => obj("title") match {
            case JsString(value) => Article(value)
          }
        }
      }
    }
  }

  def getArticle(id: String) = {
    client.runRequest(s"articles/$id").map{ response =>
      response.fields("article") match {
        case JsObject(obj) => obj("title") match {
          case JsString(value) => Article(value)
        }
      }
    }
  }
}
