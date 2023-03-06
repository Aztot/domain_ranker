package com.ranked.domain.model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.bson.types.ObjectId
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat, enrichAny}

trait RankedDomainJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit lazy val vstatData: RootJsonFormat[VstatData]                      = jsonFormat1(VstatData.apply)
  implicit lazy val domainData: RootJsonFormat[DomainData]                    = jsonFormat7(DomainData.apply)
  implicit lazy val pagePropsPageFormat: RootJsonFormat[PagePropsData]        = jsonFormat1(PagePropsData.apply)
  implicit lazy val trustpilotReviewFormat: RootJsonFormat[TrustpilotReview]  = jsonFormat2(TrustpilotReview.apply)

  implicit lazy val trustpilotCategoryPageData: RootJsonFormat[TrustpilotCategoryPageData] =
    jsonFormat1(TrustpilotCategoryPageData.apply)
  implicit lazy val recentlyReviewedBusinessUnitsData: RootJsonFormat[RecentlyReviewedBusinessUnitsData] =
    jsonFormat3(RecentlyReviewedBusinessUnitsData.apply)


  implicit lazy val objectId: RootJsonFormat[ObjectId] = new RootJsonFormat[ObjectId] {
    override def write(obj: ObjectId): JsValue = obj.toString.toJson

    override def read(json: JsValue): ObjectId = new ObjectId(json.convertTo[String])
  }
}
