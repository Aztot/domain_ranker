package com.ranked.domain

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ranked.domain.model.RankedDomainJsonProtocol
import spray.json.enrichAny

import scala.concurrent.ExecutionContext

class RankedDomainRoutes(rankedDomainController: RankedDomainController)(implicit ec: ExecutionContext) extends RankedDomainJsonProtocol {
  val route: Route = {
    pathPrefix("ranked-domains") {
      path("category" / Segment) { category: String =>
        get {
          complete(rankedDomainController.getRankedDomainsByCategory(category).map(_.toJson))
        }
      } ~ pathEnd {
        get {
          complete(rankedDomainController.getRankedDomainList.map(_.toJson))
        }
      }
    } ~ path("health") {
      get {
        complete("OK")
      }
    }
  }
}
