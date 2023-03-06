package com.ranked.domain

import akka.actor.Actor
import com.ranked.domain.RankedDomainScheduler.ProcessByCategory
import com.ranked.domain.config.RankedDomainConfig

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class RankedDomainScheduler(rankedDomainService: RankedDomainService,
                            rdConfig: RankedDomainConfig)
                           (implicit ec: ExecutionContext) extends Actor {

  def receive: Receive = {
    case RankedDomainScheduler.ScheduleProcessing =>
      self ! ProcessByCategory(rdConfig.categories)
    case ProcessByCategory(categories) =>
      categories match {
        case Nil =>
          println("All categories processed successfully")
        case category :: Nil =>
          println(s"Processing category $category")
          rankedDomainService.scrapAndSaveDomainList(category).onComplete {
            case Success(_) =>
              println("All categories processed successfully")
            case Failure(exception) =>
              exception.printStackTrace()
              println(s"Error during processing category $category")
              println("All categories processed successfully")
          }
        case category :: categories =>
          println(s"Processing category $category")
          rankedDomainService.scrapAndSaveDomainList(category).onComplete {
            case Success(_) =>
              self ! ProcessByCategory(categories)
            case Failure(exception) =>
              exception.printStackTrace()
              println(s"Error during processing category $category")
              self ! ProcessByCategory(categories)
          }
      }
  }
}

object RankedDomainScheduler {
  case object ScheduleProcessing
  case class ProcessByCategory(categories: List[String])
}
