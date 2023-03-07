package com.ranked.domain

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import com.ranked.domain.client.{TrustpilotHttpClient, VstatHttpClient}
import com.ranked.domain.config.RankedDomainConfig
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.mongodb.scala.MongoClient

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{Duration, DurationInt}

object RankedDomainServer extends App {
  implicit private val system: ActorSystem                        = ActorSystem()
  implicit private val executionContext: ExecutionContextExecutor = system.dispatcher

  private val logger                    = Logger.apply(getClass.getName)
  private val config                    = ConfigFactory.load()
  private val rdConfig                  = RankedDomainConfig(config)
  private val mongoClient: MongoClient  = MongoClient(rdConfig.dbHost)

  private val vstatClient             = new VstatHttpClient
  private val trustpilotClient        = new TrustpilotHttpClient
  private val rankedDomainRepository  = new DefaultRankedDomainRepository(mongoClient)
  private val rankedDomainService     = new DefaultRankedDomainService(rankedDomainRepository, trustpilotClient, vstatClient)

  private val rankedListController  = new RankedDomainController(rankedDomainService)
  private val routes                = new RankedDomainRoutes(rankedListController).route

  private val schedulerActor =
    system.actorOf(Props.create(classOf[RankedDomainScheduler], rankedDomainService, rdConfig, executionContext))

  private val cancellable =
    system
      .scheduler
      .scheduleWithFixedDelay(Duration.Zero, 5.minutes, schedulerActor, RankedDomainScheduler.ScheduleProcessing)

  Http().newServerAt("0.0.0.0", 10202).bind(routes)

  logger.info(s"Server online at http://0.0.0.0:10202/")

  system.whenTerminated.onComplete { _ =>
    logger.info(s"Server stops")

    cancellable.cancel()
    system.terminate()
  }
}
