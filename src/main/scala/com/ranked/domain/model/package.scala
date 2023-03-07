package com.ranked.domain

import java.time.Instant

package object model {
  case class TrustpilotCategoryPageData(pageProps: PagePropsData)

  case class PagePropsData(recentlyReviewedBusinessUnits: List[RecentlyReviewedBusinessUnitsData])

  case class RecentlyReviewedBusinessUnitsData(identifyingName: String, review: Option[TrustpilotReview], numberOfReviews: Int) // review is optional because there are domains without review

  case class TrustpilotReview(id: String, text: String, createdAt: Instant)

  case class VstatData(traffic: Int)
}
