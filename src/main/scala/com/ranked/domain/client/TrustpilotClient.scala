package com.ranked.domain.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.ranked.domain.model.{RankedDomainJsonProtocol, TrustpilotCategoryPageData}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

trait TrustpilotClient {
  def getLatestReviewedDomains(category: String): Future[TrustpilotCategoryPageData]
}

class TrustpilotHttpClient(implicit ec: ExecutionContext, as: ActorSystem)
  extends TrustpilotClient with RankedDomainJsonProtocol {

  def getLatestReviewedDomains(category: String): Future[TrustpilotCategoryPageData] = {
    val vstatUri =
      "https://www.trustpilot.com/_next/data/categoriespages-consumersite-3487/categories/" + category + ".json?sort=latest_review&categoryId=" + category

    val responseFuture: Future[HttpResponse] = Http()
      .singleRequest(HttpRequest(uri = vstatUri))


    responseFuture.flatMap {
      case response@HttpResponse(StatusCodes.OK, _, _, _) =>
        Unmarshal(response.entity).to[String].map { str =>
          str.parseJson.convertTo[TrustpilotCategoryPageData]
        }
      case _ => throw new Exception("Data not found")
    }
  }

}