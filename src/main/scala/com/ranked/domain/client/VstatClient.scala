package com.ranked.domain.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.ranked.domain.model.{RankedDomainJsonProtocol, VstatData}
import io.lemonlabs.uri.Uri

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex

trait VstatClient {
  def getTrafficCount(domain: String): Future[VstatData]
}

class VstatHttpClient(implicit ec: ExecutionContext, as: ActorSystem) extends VstatClient with RankedDomainJsonProtocol {
  def getTrafficCount(domain: String): Future[VstatData] = {
    val firstHeader = createHeader("authority", "web.vstat.info")
    val secondHeader = createHeader("cookie", "vstat_session=ErJt7YU24evVK9RUNNqcn95FhC5yai0jHAHmnVwN;")
    val vstatUri = Uri.parse("https://web.vstat.info/" + domain).toString

    val request = HttpRequest(uri = vstatUri).withHeaders(firstHeader, secondHeader)

    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val pattern: Regex = "MONTHLY_VISITS.*data-smvisits=\"(\\d+)\"".r

    responseFuture.flatMap {
      case response@HttpResponse(StatusCodes.OK, _, _, _) =>
        Unmarshal(response.entity).to[String].map { traffic =>
          val numberOfMonthlyVisits = pattern.findFirstMatchIn(traffic).flatMap { num =>
            Try(num.group(1).toInt).toOption
          }
          VstatData(numberOfMonthlyVisits.getOrElse(-1))
        }
      case _ =>
        throw new Exception("Data not found")
    }
  }

  private def createHeader(name: String, value: String): HttpHeader = {
    HttpHeader
      .parse(name, value)
      .asInstanceOf[ParsingResult.Ok] // cause headers are hardcoded using asInstanceOf is safe
      .header
  }
}
