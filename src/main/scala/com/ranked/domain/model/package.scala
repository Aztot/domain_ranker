package com.ranked.domain

package object model {
  case class TrustpilotCategoryPageData(pageProps: PagePropsData)

  case class PagePropsData(recentlyReviewedBusinessUnits: List[RecentlyReviewedBusinessUnitsData])

  case class RecentlyReviewedBusinessUnitsData(identifyingName: String, review: TrustpilotReview, numberOfReviews: Int)

  case class TrustpilotReview(id: String, text: String)

  case class VstatData(traffic: Int)
}
