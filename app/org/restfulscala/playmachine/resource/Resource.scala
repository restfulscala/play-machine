package org.restfulscala.playmachine.resource

import play.api.http.{MediaRange, HttpVerbs}
import play.api.mvc._

trait Resource[R, RequestParams] extends Controller with HttpVerbs {

  def allowedMethods : Set[String] = Set(GET, HEAD)

  // idea : write async version
  def isAuthorized(request : Request[_], requestParams: RequestParams): Boolean = true

  def isForbidden(request : Request[_], requestParams: RequestParams): Boolean = false

  def isImplemented(headers : Headers): Boolean = true

  def isContentTypeSupport(contentType : Option[String]): Boolean = true

  def isRequestEntityTooLarge(request : Request[_]) : Boolean = false

  // idea : write async version
  def isResourceExists(request : Request[_], requestParams: RequestParams) : Option[R]

  // idea : write async version
  def isResourcePreviouslyExisted(request : Request[_], requestParams: RequestParams) : Boolean = false

  def handlePost(request : Request[_], requestParams: RequestParams) : Either[Int, R] = Left(500)

  def handlePut(request : Request[_], requestParams: RequestParams) : Either[String, R] = Left("Not implemented by resource")

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

  def extractRequestParams(request: Request[_], pathParams: Seq[PathParam]): Option[RequestParams]

  def handleBadRequest(request : Request[_], pathParams: Seq[PathParam]): Result = {
    extractRequestParams(request, pathParams) match {
      case Some(requestParams) => handleUnauthorized(request, requestParams)
      case None => BadRequest
    }
  }

  def handleUnauthorized(request : Request[_], requestParams: RequestParams): Result = {
  	isAuthorized(request, requestParams) match {
      case true  => handleForbidden(request, requestParams)
      case false => Results.Unauthorized
    }
  }

  def handleForbidden(request : Request[_], requestParams: RequestParams): Result = {
  	!isForbidden(request, requestParams) match {
      case true  => handleNotImplemented(request, requestParams)
      case false => Results.Forbidden
    }
  }

  def handleNotImplemented(request : Request[_], requestParams: RequestParams): Result = {
  	isImplemented(request.headers) match {
      case true  => handleUnsupportedMediaType(request, requestParams)
      case false => Results.NotImplemented
    }
  }

  def handleUnsupportedMediaType(request : Request[_], requestParams: RequestParams): Result = {
  	isContentTypeSupport(request.contentType) match {
      case true  => handleRequestEntityTooLarge(request, requestParams)
      case false => Results.UnsupportedMediaType
    }
  }

  def handleRequestEntityTooLarge(request : Request[_], requestParams: RequestParams): Result = {
  	!isRequestEntityTooLarge(request) match {
      case true  => handleMethod(request, requestParams)
      case false => Results.EntityTooLarge
    }
  }

  def handleMethod(request : Request[_], requestParams: RequestParams): Result = {
  	request.method match {
      case OPTIONS => Results.Ok.withHeaders("Allow" -> allowedMethods.mkString(", "))
      case HEAD => handleHead(request, requestParams)
      case GET => isResourceExists(request, requestParams) match {
        case Some(resource) => render(handleGet(resource))(request)
        case None => NotFound
      }
      case POST => handlePOST(request, requestParams)
      case PUT => handleWrites(request, requestParams)
    }
  }

  def handleResourceExists(request : Request[_], requestParams: RequestParams): Result = {
  	//  from now on, we will follow an over simplified version of the header flow...because HACKATHON!
  	isResourceExists(request, requestParams) match {
      case Some(r)  => handleReads(request, requestParams, r)
      case None     => handleWrites(request, requestParams)
    }
  }

  def handleReads(request : Request[_], requestParams: RequestParams, resource : R): Result = {
    request.method match {
      case GET  => render(handleGet(resource))(request)
      case HEAD => handleHead(request, requestParams)
      case _ => BadRequest
    }
  }

  def handleGet(resource: R): PartialFunction[MediaRange, Result] = {
    case _ => InternalServerError
  }

  def handleHead(request : Request[_], requestParams: RequestParams): Result = {
    NoContent
  }

  def handleWrites(request : Request[_], requestParams: RequestParams): Result = {
  	request.method == "PUT" match {
      case true  => handlePut(request, requestParams) match {
      	case Left(s) => Results.InternalServerError(s)
      	case Right(resource) => Results.Ok // add location header and deal with creation
      }
      case false => handleResourcePreviouslyExisted(request, requestParams)
    }
  }

  def handleResourcePreviouslyExisted(request : Request[_], requestParams: RequestParams): Result = {
  	isResourcePreviouslyExisted(request, requestParams) match {
      case true  => handleResourceMovedPermanently(request, requestParams)
      case false => handlePOST(request, requestParams)
    }
  }

  def handlePOST(request : Request[_], requestParams: RequestParams): Result = {
  	request.method == "POST" match {
      case true  => handlePost(request, requestParams) match {
      	case Left(s) => Results.Status(s)
      	case Right(resource) => Results.Ok // add location header and deal with creation
      }
      case false => Results.NotFound
    }
  }

  def handleResourceMovedPermanently(request : Request[_], requestParams: RequestParams): Result = ???

}