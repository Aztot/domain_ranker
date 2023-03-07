package com.ranked.domain

import com.ranked.domain.client.{TrustpilotClient, VstatClient}
import com.ranked.domain.model.{DomainData, RecentlyReviewedBusinessUnitsData, TrustpilotCategoryPageData, VstatData}
import org.bson.types.ObjectId

import scala.concurrent.{ExecutionContext, Future}

trait RankedDomainService {
  def scrapAndSaveDomainList(category: String): Future[Unit]
  def getRankedDomainList: Future[Seq[DomainData]]
  def getRankedDomainsByCategory(category: String): Future[Seq[DomainData]]
}

class DefaultRankedDomainService(rankedDomainRepository: RankedDomainRepository,
                                 trustpilotClient: TrustpilotClient,
                                 vstatClient: VstatClient)(implicit ec: ExecutionContext)
  extends RankedDomainService {
  def scrapAndSaveDomainList(category: String): Future[Unit] = {
    for {
      listDomain <- trustpilotClient.getLatestReviewedDomains(category)
      listDomainWithTraffic <- buildDomainDataList(listDomain, category)
      _ <- Future.sequence(listDomainWithTraffic.map(rankedDomainRepository.setRankedDomain))
    } yield ()
  }


  private def buildDomainDataList(categoryPageData: TrustpilotCategoryPageData, category: String): Future[List[DomainData]] = Future.sequence {
    categoryPageData
      .pageProps
      .recentlyReviewedBusinessUnits
      .filter(_.review.nonEmpty)
      .map(domain => vstatClient.getTrafficCount(domain.identifyingName).map(toDomainData(domain, category)))
  }

  private def toDomainData(domainData: RecentlyReviewedBusinessUnitsData, category: String)(trafficData: VstatData): DomainData = {
    DomainData(domainData.identifyingName, domainData.review.get, domainData.numberOfReviews, trafficData.traffic, category)
  }

  override def getRankedDomainList: Future[Seq[DomainData]] =
    rankedDomainRepository.getRankedDomainList

  override def getRankedDomainsByCategory(category: String): Future[Seq[DomainData]] =
    rankedDomainRepository.getRankedDomainsByCategory(category)
}
