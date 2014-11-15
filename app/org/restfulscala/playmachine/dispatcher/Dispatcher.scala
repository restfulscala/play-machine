package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.resource.{Resource, PathParam}
import play.api.mvc._

class Dispatcher(routes: Seq[Route])
  extends (RequestHeader => Option[Handler]) with Results {

  final def apply(request: RequestHeader): Option[Handler] = {
    dispatch(request) match {
      case Some((resource, params)) => Some(resource.handleRequest(params))
      case None => Some(Action(NotFound))
    }
  }

  protected def dispatch(request: RequestHeader): Option[(Resource[_], Seq[PathParam])] =
    routes collectFirst Function.unlift(Route.matches(request))

}

object Dispatcher {
  def from(routes: Route*): Dispatcher = {
    new Dispatcher(routes)
  }
}