package com.ranked.domain.model

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

case class DomainData(_id: ObjectId,
                      domain: String,
                      latestReview: TrustpilotReview,
                      totalReviewCount: Int,
                      traffic: Int,
                      category: String,
                      latestReviewCount: Int)

object DomainData {
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[DomainData], classOf[TrustpilotReview]), DEFAULT_CODEC_REGISTRY)

  def apply(domain: String,
            latestReview: TrustpilotReview,
            totalReviewCount: Int,
            traffic: Int,
            category: String,
            latestReviewCount: Int = 1): DomainData =
    DomainData(ObjectId.get(), domain, latestReview, totalReviewCount, traffic, category, latestReviewCount)
}



