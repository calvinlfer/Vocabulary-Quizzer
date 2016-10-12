import javax.inject.Inject

import filters.ScoreFilter
import play.api.http.HttpFilters
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

/**
 * Sets up Filters that are applied to the request and result
 * The HttpFilters trait sets up a filter chain with the filters that you specify
 *
 * @param gzip is used to gzip responses sent to the client to speed things up
 * @param scoreFilter is used to look at the score in the session and show it in the body
 */
class Filters @Inject() (gzip: GzipFilter, scoreFilter: ScoreFilter) extends HttpFilters {
  // filters are specified in the order that they should be applied
  // the Security Headers Filter adds a number of header-based security
  // checks and policies
  // we add the scoreFilter last which modifies the response based on the score
  // (used by the /quiz/:lang/check... endpoint)
  val filters = Seq(gzip, SecurityHeadersFilter(), scoreFilter)
}
