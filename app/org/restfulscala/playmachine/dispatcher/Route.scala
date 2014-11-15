package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.mvc.RequestHeader

case class Route(pathMatch: PathMatch, resource: Resource)
object Route {
  def matches(request: RequestHeader)(route: Route): Option[(Resource, Seq[PathParam])] = {
    val pathParams = PathMatch.matches(route.pathMatch)(request.path)
    pathParams map (route.resource -> _)
  }
}
