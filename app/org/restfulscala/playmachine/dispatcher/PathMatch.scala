package org.restfulscala.playmachine.dispatcher

import org.restfulscala.playmachine.dispatcher.PathMatch.PathElement
import org.restfulscala.playmachine.resource.PathParam

import scala.annotation.tailrec

import scala.language.implicitConversions

case class PathMatch(elements: Vector[PathMatch.PathElement]) {
  def /(pathElement: PathElement): PathMatch = copy(elements :+ pathElement)
}
object PathMatch {
  sealed trait PathElement {
    def /(pathElement: PathElement): PathMatch = PathMatch(Vector(this, pathElement))
  }
  case class Seg(s: String) extends PathElement
  case class Param(n: String) extends PathElement
  case object Empty extends PathElement

  implicit def PathElement2PathMatch(pathElement: PathElement) =
    PathMatch(Vector(pathElement))

  implicit def string2Seg(pathSeg: String): PathElement = Seg(pathSeg)

  def matches(pathMatch: PathMatch)(path: String): Option[Seq[PathParam]] = {
    val elements = path.split('/').toList.filterNot(_.isEmpty) map (Some(_))
    val toMatch = pathMatch.elements.zipAll(elements, Empty, None)
    matchesIter(toMatch.toList, Some(Vector.empty))
  }
  @tailrec
  private def matchesIter(
    rest: List[(PathElement, Option[String])],
    acc: Option[Vector[PathParam]]): Option[Vector[PathParam]] = acc match {
    case Some(a) ⇒
      rest match {
        case (pathElement, Some(s)) :: tail ⇒ pathElement match {
          case Seg(seg) ⇒
            if (seg == s) matchesIter(tail, acc)
            else None
          case Param(param) ⇒
            matchesIter(tail, Some(a :+ PathParam(param, s)))
          case Empty ⇒ None
        }
        case Nil ⇒ acc
        case _ => None
      }
    case None ⇒ None
  }
}
