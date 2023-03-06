package com.ranked.domain.config

import com.typesafe.config.Config

import scala.jdk.CollectionConverters._

case class RankedDomainConfig(categories: List[String], dbHost: String)

object RankedDomainConfig {
  def apply(config: Config): RankedDomainConfig = {
    val categories = config.getStringList("categories").asScala.toList
    val mongoDbConfig = config.getString("mongo.dbHost")

    RankedDomainConfig(categories, mongoDbConfig)
  }
}

