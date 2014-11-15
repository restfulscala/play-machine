package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.dispatcher.PathMatch.PathElement
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.mvc.RequestHeader

import scala.language.existentials

case class Route(pathMatch: PathMatch, resource: Resource[_, _]) {
}
object Route {

  implicit class PathMatchRouteBuilder(val pathMatch: PathMatch) extends AnyVal {
    def :~>(resource: Resource[_, _]): Route = Route(pathMatch, resource)
  }

  implicit class PathElementRouteBuilder(val pathElement: PathElement) extends AnyVal {
    def :~>(resource: Resource[_, _]): Route = Route(PathMatch(Vector(pathElement)), resource)
  }

  def matches(request: RequestHeader)(route: Route): Option[(Resource[_, _], Seq[PathParam])] = {
    val pathParams = PathMatch.matches(route.pathMatch)(request.path)
    pathParams map (route.resource -> _)
  }
}
