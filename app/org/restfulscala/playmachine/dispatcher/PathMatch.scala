package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.resource.PathParam

import scala.annotation.tailrec

case class PathMatch(elements: List[PathMatch.PathElement])
object PathMatch {
  sealed trait PathElement
  case class Seg(s: String) extends PathElement
  case class Param(n: String) extends PathElement
  case object Empty extends PathElement
  def matches(pathMatch: PathMatch)(path: String): Option[Seq[PathParam]] = {
    val elements = path.split('/').toList.filterNot(_.isEmpty)
    val toMatch = pathMatch.elements.zipAll(elements, Empty, "")
    matchesIter(toMatch, Some(Vector.empty))
  }
  @tailrec
  private def matchesIter(
    rest: List[(PathElement, String)],
    acc: Option[Vector[PathParam]]): Option[Vector[PathParam]] = acc match {
    case Some(a) ⇒
      rest match {
        case (pathElement, s) :: tail ⇒ pathElement match {
          case Seg(seg) ⇒
            if (seg == s) matchesIter(tail, acc)
            else None
          case Param(param) ⇒
            matchesIter(tail, Some(a :+ PathParam(param, s)))
          case Empty ⇒ None
        }
        case Nil ⇒ acc
      }
    case None ⇒ None
  }
}
