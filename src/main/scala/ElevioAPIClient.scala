import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

trait ElevioAPIClient {
  def runRequest(path: String, param: Seq[Query] = Seq.empty): Future[JsObject]
}

class ElevioAPIClientImpl()(implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext)
  extends ElevioAPIClient
    with ArticleJsonSupport{

  private val key = sys.env("ELEVIO_KEY")

  private val token = s"Bearer ${sys.env("ELEVIO_TOKEN")}"

  val URI = "https://api.elev.io/v1"

  def runRequest(path: String, params: Seq[Query] = Seq.empty): Future[JsObject] = {
    val key_header = RawHeader("x-api-key", key)
    val token_header = RawHeader("Authorization", token)

    var uri = Uri(s"$URI/$path")
    uri = params.foldLeft(uri)((uri, query) => uri.withQuery(query))

    Http().singleRequest(HttpRequest(uri = uri).withHeaders(key_header, token_header)).flatMap{ response =>
      Unmarshal(response).to[JsObject]
    }
  }

}
