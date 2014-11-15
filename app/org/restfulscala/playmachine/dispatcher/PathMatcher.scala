package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.dispatcher.PathMatcher.PathMatchingError
import shapeless.HList

trait PathMatcher[PathParams <: HList]
  extends (String => Either[PathMatchingError, PathParams])

object PathMatcher {
  sealed trait PathMatchingError
  case class UnmatchedPath(path: String)
}
