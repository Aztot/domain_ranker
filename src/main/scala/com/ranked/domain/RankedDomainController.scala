package com.ranked.domain

import com.ranked.domain.model.DomainData

import scala.concurrent.Future

class RankedDomainController(rankedDomainService: RankedDomainService) {
  def getRankedDomainList: Future[Seq[DomainData]] =
    rankedDomainService.getRankedDomainList

  def getRankedDomainsByCategory(category: String): Future[Seq[DomainData]] =
    rankedDomainService.getRankedDomainsByCategory(category)
}
