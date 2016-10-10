import javax.inject.Provider

import com.google.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent._

/**
 * A Play error handler discoverable via dependency injection
 * It extends the default http error handler trait which is the hook to error-handling customization in Play
 * @param env
 * @param config
 * @param sourceMapper
 * @param router
 */
class ErrorHandler @Inject() (env: Environment, config: Configuration,
  sourceMapper: OptionalSourceMapper, router: Provider[Router])
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  // intercept play's default behavior when an error occurs during communication with the client
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] =
    // return 404 Not Found with String representation of the result
    Future.successful(NotFound("Could not find " + request))
}
