package filters

import play.api.libs.concurrent.Execution.Implicits._ // we require an execution context for manipulating Futures
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{ Filter, RequestHeader, Result }

import scala.concurrent.Future

/**
 * A filter example that is designed to grab the score from Play's session and display in the request body
 * This is done for learning purposes only
 */
class ScoreFilter extends Filter {

  // The nextFilter function represents the next request handler in the chain (similar idea to Node's middleware)
  // the next handler is usually a filter
  // The request header of the current request is provided as well
  override def apply(nextFilter: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    // call the next filter with the current request header as-is
    val result: Future[Result] = nextFilter(rh)

    val StatusCodeOK = 200
    val StatusCodeNotAcceptable = 406
    // Look at the result returned by the next request handler (happens to be Controller's Action here)
    result map { res =>
      // observe the status code and modify the response body
      if (res.header.status == StatusCodeOK || res.header.status == StatusCodeNotAcceptable) {
        // grab the current correct and wrong # from the Play's client-side sessions
        val correct = res.session(rh).get("correct").getOrElse(0)
        val wrong = res.session(rh).get("wrong").getOrElse(0)
        val scoreMessage = s"\nYour current score is: $correct correct answers and $wrong wrong answers"
        val updatedBody = res.body andThen Enumerator(scoreMessage.getBytes("UTF-8"))
        // the response is a case class so we only update the body member
        res.copy(body = updatedBody)
      } else res
    }
  }
}
