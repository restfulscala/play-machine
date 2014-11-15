package org.restfulscala.playmachine.resource

import play.api.mvc.{Action, EssentialAction, Controller, Results, Request}
import org.restfulscala.playmachine.resource._

trait Resource extends Controller {

  // TODO use proper HttpMethod case class
  def allowedMethods : Set[String] = Set("GET", "HEAD")

  def isMalformed(request : Request[_], pathParams: Seq[PathParam]) : Boolean = false

  def isAuthorized(request : Request[_], pathParams: Seq[PathParam]) : Boolean = false

  def handleRequest(pathParams: Seq[PathParam]): EssentialAction = Action { request =>
  	???

  }

  def handleAllowedMethods(request : Request[_], pathParams: Seq[PathParam]): EssentialAction = {
  	allowedMethods.contains(request.method) match {
      case true => handleBadRequest(request, pathParams)
      case false => Action {request => Results.MethodNotAllowed}
    }
  }

  def handleBadRequest(request : Request[_], pathParams: Seq[PathParam]): EssentialAction = {
  	(!isMalformed(request, pathParams)) match {
      case true => handleUnauthorized(request, pathParams)
      case false => Action {request => Results.BadRequest}
    }
  }

  def handleUnauthorized(request : Request[_], pathParams: Seq[PathParam]): EssentialAction = {
  	(!isAuthorized(request, pathParams)) match {
      case true => handleForbidden(request, pathParams)
      case false => Action {request => Results.Unauthorized}
    }
  }

  def handleForbidden(request : Request[_], pathParams: Seq[PathParam]): EssentialAction = ???

}
