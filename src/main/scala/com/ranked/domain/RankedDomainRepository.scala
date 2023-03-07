package com.ranked.domain

import com.ranked.domain.model.DomainData
import com.typesafe.scalalogging.Logger
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, MongoWriteException}

import scala.concurrent.{ExecutionContext, Future}

trait RankedDomainRepository {
  def setRankedDomain(data: DomainData): Future[Unit]

  def insertNewDomain(domainData: DomainData): Future[Unit]

  def getRankedDomainList: Future[Seq[DomainData]]

  def getRankedDomainsByCategory(category: String): Future[Seq[DomainData]]
}

class DefaultRankedDomainRepository(mongoClient: MongoClient)
                                   (implicit ec: ExecutionContext) extends RankedDomainRepository {

  private val logger: Logger = Logger.apply(getClass.getName)

  private val database: MongoDatabase =
    mongoClient.getDatabase("ranked_domain").withCodecRegistry(DomainData.codecRegistry)

  private val collection: MongoCollection[DomainData] =
    database.getCollection("ranked_domain_collection") // rename to ran_dom_data

  def setRankedDomain(data: DomainData): Future[Unit] = {
    val updateOperations = combine(
      inc("latestReviewCount", 1),
      set("totalReviewCount", data.totalReviewCount),
      set("traffic", data.traffic),
      set("latestReview", data.latestReview))

    val filter = and(equal("domain", data.domain), notEqual("latestReview.id", data.latestReview.id))

    collection
      .findOneAndUpdate(filter, updateOperations)
      .toFuture()
      .flatMap {
        case null => insertNewDomain(data)
        case _ => Future.successful(())
      }.recoverWith { case e =>
      e.printStackTrace()
      Future.failed(e)
    }
  }

  def insertNewDomain(domainData: DomainData): Future[Unit] = {
    collection
      .insertOne(domainData)
      .toFutureOption()
      .map(_ => ())
      .recover {
        case e: MongoWriteException if e.getMessage.contains("E11000 duplicate key error collection") =>
          logger.info(s"Data with id ${domainData._id.toString} is already exists, domain is: ${domainData.domain}, category: ${domainData.category}")
        case _ =>
          ()
      }
  }


  def getRankedDomainList: Future[Seq[DomainData]] = {
    collection
      .find()
      .sort(descending("latestReviewCount", "traffic"))
      .toFuture()
  }

  def getRankedDomainsByCategory(category: String): Future[Seq[DomainData]] = {
    collection
      .find(equal("category", category))
      .sort(descending("latestReviewCount", "traffic"))
      .toFuture()
  }
}