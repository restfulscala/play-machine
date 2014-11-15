package org.restfulscala.playmachine.resource

import play.api.mvc.{Action, EssentialAction, Controller, Results, Request, Result, Headers}
import org.restfulscala.playmachine.resource._

trait Resource extends Controller {

  // TODO use proper HttpMethod case class
  def allowedMethods : Set[String] = Set("GET", "HEAD")

  def isMalformed(request : Request[_], pathParams: Seq[PathParam]): Boolean = false

  def isAuthorized(request : Request[_], pathParams: Seq[PathParam]): Boolean = true

  def isForbidden(request : Request[_], pathParams: Seq[PathParam]): Boolean = false

  def isImplemented(headers : Headers): Boolean = true

  def isContentTypeSupport(contentType : Option[String]): Boolean = true

  def isRequestEntityTooLarge(request : Request[_]) : Boolean = false

  def handleRequest(pathParams: Seq[PathParam]): EssentialAction = Action { request =>
  	// Bootstrap decision tree
  	handleAllowedMethods(request, pathParams)
  }

  def handleAllowedMethods(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	allowedMethods.contains(request.method) match {
      case true  => handleBadRequest(request, pathParams)
      case false => Results.MethodNotAllowed
    }
  }

  def handleBadRequest(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	!isMalformed(request, pathParams) match {
      case true  => handleUnauthorized(request, pathParams)
      case false => Results.BadRequest
    }
  }

  def handleUnauthorized(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	isAuthorized(request, pathParams) match {
      case true  => handleForbidden(request, pathParams)
      case false => Results.Unauthorized
    }
  }

  def handleForbidden(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	!isForbidden(request, pathParams) match {
      case true  => handleNotImplemented(request, pathParams)
      case false => Results.Forbidden
    }
  }

  def handleNotImplemented(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	isImplemented(request.headers) match {
      case true  => handleUnsupportedMediaType(request, pathParams)
      case false => Results.NotImplemented
    }
  }

  def handleUnsupportedMediaType(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	isContentTypeSupport(request.contentType) match {
      case true  => handleRequestEntityTooLarge(request, pathParams)
      case false => Results.UnsupportedMediaType
    }
  }

  def handleRequestEntityTooLarge(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	!isRequestEntityTooLarge(request) match {
      case true  => handleIsOptions(request, pathParams)
      case false => Results.EntityTooLarge
    }
  }

  def handleIsOptions(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	request.method == "OPTIONS" match {
      case true  => Results.Ok.withHeaders("Allow" -> allowedMethods.mkString(", "))
      case false => ???
    }
  }
}