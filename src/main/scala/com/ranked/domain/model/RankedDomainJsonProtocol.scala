package com.ranked.domain.model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.bson.types.ObjectId
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat, enrichAny}

import java.time.Instant

trait RankedDomainJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit lazy val vstatData: RootJsonFormat[VstatData]                      = jsonFormat1(VstatData.apply)
  implicit lazy val domainData: RootJsonFormat[DomainData]                    = jsonFormat7(DomainData.apply)
  implicit lazy val pagePropsPageFormat: RootJsonFormat[PagePropsData]        = jsonFormat1(PagePropsData.apply)

  implicit lazy val trustpilotCategoryPageData: RootJsonFormat[TrustpilotCategoryPageData] =
    jsonFormat1(TrustpilotCategoryPageData.apply)
  implicit lazy val recentlyReviewedBusinessUnitsData: RootJsonFormat[RecentlyReviewedBusinessUnitsData] =
    jsonFormat3(RecentlyReviewedBusinessUnitsData.apply)

  implicit lazy val trustpilotReviewFormat: RootJsonFormat[TrustpilotReview] = new RootJsonFormat[TrustpilotReview] {
    override def read(jsValue: JsValue): TrustpilotReview = {
      val fields = jsValue.asJsObject.fields
      val text = fields("text").convertTo[String]
      val id = fields("id").convertTo[String]
      val createdAt = fields("date").asJsObject.fields("createdAt").convertTo[Instant]

      TrustpilotReview(id, text, createdAt)
    }

    override def write(obj: TrustpilotReview): JsValue = obj.toJson(jsonFormat3(TrustpilotReview.apply))
  }

  implicit lazy val instantFormat: RootJsonFormat[Instant] = new RootJsonFormat[Instant] {
    override def read(json: JsValue): Instant = Instant.parse(json.convertTo[String])

    override def write(obj: Instant): JsValue = obj.toString.toJson
  }


  implicit lazy val objectId: RootJsonFormat[ObjectId] = new RootJsonFormat[ObjectId] {
    override def write(obj: ObjectId): JsValue = obj.toString.toJson

    override def read(json: JsValue): ObjectId = new ObjectId(json.convertTo[String])
  }
}
