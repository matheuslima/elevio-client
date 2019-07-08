import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import spray.json.{JsArray, JsObject, JsString}

import scala.concurrent.Future
import scala.concurrent.duration._



class ArticleAPITest
  extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with MockFactory
    with ArticleJsonSupport {

  val client = stub[ElevioAPIClient]

  val route: Route = new ArticleAPI(new ArticleService(client)).route()

  val articles = JsObject("articles" -> JsArray(
    JsObject("title" -> JsString("Test1")),
    JsObject("title" -> JsString("Test2")),
    JsObject("title" -> JsString("Test3"))
  ))

  val search = JsObject("results" -> JsArray(
    JsObject("title" -> JsString("Test1")),
    JsObject("title" -> JsString("Test2")),
    JsObject("title" -> JsString("Test3"))
  ))

  val article = JsObject("article" -> JsObject("title" -> JsString("Test4")))

  val articlesExpected = Seq(Article("Test1"), Article("Test2"), Article("Test3"))
  val articleExpected = Article("Test4")

  implicit val timeout = RouteTestTimeout(5.seconds dilated)


  "The service" should {
    "return all articles for the GET request to the articles path" in {
      (client.runRequest _).when("articles", Seq(Query("status" -> "published"))).returning(Future.successful(articles))
      Get("/articles") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[Article]] should equal (articlesExpected)
      }
    }
    "return an article for the GET request to the article path for a given id" in {
      (client.runRequest _).when("articles/123", Seq.empty[Query]).returning(Future.successful(article))
      Get("/article/123") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Article] should equal (articleExpected)
      }
    }
    "return article for the GET request to the search path for a given term" in {
      (client.runRequest _).when("search/en", Seq(Query("query" -> "Test"))).returning(Future.successful(search))
      Get("/search/Test") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[Article]] should equal (articlesExpected)
      }
    }
  }
}
