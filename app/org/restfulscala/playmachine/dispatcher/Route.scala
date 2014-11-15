package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.resource.Resource
import shapeless.HList

case class Route[PathParams <: HList](
  pathMatcher: PathMatcher[PathParams],
  resource: Resource[PathParams])
