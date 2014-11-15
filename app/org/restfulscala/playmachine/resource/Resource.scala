package org.restfulscala.playmachine.resource

import play.api.mvc.{Action, EssentialAction, Controller, Results, Request}
import org.restfulscala.playmachine.resource._

trait Resource extends Controller {

  // TODO use proper HttpMethod case class
  def allowedMethods : Set[String] = Set("GET", "HEAD")

  def handleRequest(pathParams: Seq[PathParam]): EssentialAction = Action { request =>
  	???

  }

  def handleAllowedMethods(request : Request[_]): EssentialAction = {
  	allowedMethods.contains(request.method) match {
      case true => handleBadRequest(request)
      case false => Action {request => Results.MethodNotAllowed}
    }
  }

  def handleBadRequest(request : Request[_]): EssentialAction = ???

  def handleUnauthorized(request : Request[_]): EssentialAction = ???

}
