package com.ranked.domain

import akka.actor.Actor
import com.ranked.domain.RankedDomainScheduler.ProcessByCategory
import com.ranked.domain.config.RankedDomainConfig
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class RankedDomainScheduler(rankedDomainService: RankedDomainService,
                            rdConfig: RankedDomainConfig)
                           (implicit ec: ExecutionContext) extends Actor {

  private val logger: Logger = Logger.apply(getClass.getName)

  def receive: Receive = {
    case RankedDomainScheduler.ScheduleProcessing =>
      self ! ProcessByCategory(rdConfig.categories)
    case ProcessByCategory(categories) =>
      categories match {
        case Nil =>
          logger.info("All categories processed successfully")
        case category :: Nil =>
          logger.info(s"Processing category $category")
          rankedDomainService.scrapAndSaveDomainList(category).onComplete {
            case Success(_) =>
              logger.info("All categories processed successfully")
            case Failure(exception) =>
              exception.printStackTrace()
              logger.error(s"Error during processing category $category")
              logger.info("All categories processed successfully")
          }
        case category :: categories =>
          logger.info(s"Processing category $category")
          rankedDomainService.scrapAndSaveDomainList(category).onComplete {
            case Success(_) =>
              self ! ProcessByCategory(categories)
            case Failure(exception) =>
              exception.printStackTrace()
              logger.error(s"Error during processing category $category")
              self ! ProcessByCategory(categories)
          }
      }
  }
}

object RankedDomainScheduler {
  case class ProcessByCategory(categories: List[String])

  case object ScheduleProcessing
}
