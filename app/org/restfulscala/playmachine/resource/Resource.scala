package org.restfulscala.playmachine.resource

import play.api.http.{MediaRange, HttpVerbs}
import play.api.mvc._

trait Resource[R] extends Controller with HttpVerbs {

  def allowedMethods : Set[String] = Set(GET, HEAD)

  def isMalformed(request : Request[_], pathParams: Seq[PathParam]): Boolean = false

  // idea : write async version
  def isAuthorized(request : Request[_], pathParams: Seq[PathParam]): Boolean = true

  def isForbidden(request : Request[_], pathParams: Seq[PathParam]): Boolean = false

  def isImplemented(headers : Headers): Boolean = true

  def isContentTypeSupport(contentType : Option[String]): Boolean = true

  def isRequestEntityTooLarge(request : Request[_]) : Boolean = false

  // idea : write async version
  def isResourceExists(request : Request[_], pathParams: Seq[PathParam]) : Option[R]

  // idea : write async version
  def isResourcePreviouslyExisted(request : Request[_], pathParams: Seq[PathParam]) : Boolean = false

  def handlePost(request : Request[_], pathParams: Seq[PathParam]) : Either[Int, R] = Left(500)

  def handlePut(request : Request[_], pathParams: Seq[PathParam]) : Either[String, R] = Left("Not implemented by resource")

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
      case true  => handleMethod(request, pathParams)
      case false => Results.EntityTooLarge
    }
  }

  def handleMethod(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	request.method match {
      case OPTIONS => Results.Ok.withHeaders("Allow" -> allowedMethods.mkString(", "))
      case HEAD => handleHead(request, pathParams)
      case GET => isResourceExists(request, pathParams) match {
        case Some(resource) => render(handleGet(resource))(request)
        case None => NotFound
      }
      case POST => handlePOST(request, pathParams)
      case PUT => handleWrites(request, pathParams)
    }
  }

  def handleResourceExists(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	//  from now on, we will follow an over simplified version of the header flow...because HACKATHON!
  	isResourceExists(request, pathParams) match {
      case Some(r)  => handleReads(request, pathParams, r)
      case None     => handleWrites(request, pathParams)
    }
  }

  def handleReads(request : Request[_], pathParams: Seq[PathParam], resource : R): Result = {
    request.method match {
      case GET  => render(handleGet(resource))(request)
      case HEAD => handleHead(request, pathParams)
      case _ => BadRequest
    }
  }

  def handleGet(resource: R): PartialFunction[MediaRange, Result] = {
    case _ => InternalServerError
  }

  def handleHead(request : Request[_], pathParams: Seq[PathParam]): Result = {
    NoContent
  }

  def handleWrites(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	request.method == "PUT" match {
      case true  => handlePut(request, pathParams) match {
      	case Left(s) => Results.InternalServerError(s)
      	case Right(resource) => Results.Ok // add location header and deal with creation
      }
      case false => handleResourcePreviouslyExisted(request, pathParams)
    }
  }

  def handleResourcePreviouslyExisted(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	isResourcePreviouslyExisted(request, pathParams) match {
      case true  => handleResourceMovedPermanently(request, pathParams)
      case false => handlePOST(request, pathParams)
    }
  }

  def handlePOST(request : Request[_], pathParams: Seq[PathParam]): Result = {
  	request.method == "POST" match {
      case true  => handlePost(request, pathParams) match {
      	case Left(s) => Results.Status(s)
      	case Right(resource) => Results.Ok // add location header and deal with creation
      }
      case false => Results.NotFound
    }
  }

  def handleResourceMovedPermanently(request : Request[_], pathParams: Seq[PathParam]): Result = ???

}