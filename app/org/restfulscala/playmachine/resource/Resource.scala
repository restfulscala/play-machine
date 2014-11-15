package org.restfulscala.playmachine.resource

import play.api.http.{HttpVerbs, MediaRange}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait Resource[R, RequestParams] extends Controller with HttpVerbs {

  implicit def executionContext: ExecutionContext

  def allowedMethods : Set[String] = Set(GET, HEAD)

  // idea : write async version
  def isAuthorized(request : Request[_], requestParams: RequestParams): Boolean = true

  def isForbidden(request : Request[_], requestParams: RequestParams): Boolean = false

  def isImplemented(headers : Headers): Boolean = true

  def isContentTypeSupport(contentType : Option[String]): Boolean = true

  def isRequestEntityTooLarge(request : Request[_]) : Boolean = false

  // idea : write async version
  def isResourceExists(request : Request[_], requestParams: RequestParams): Future[Option[R]]

  // idea : write async version
  def isResourcePreviouslyExisted(request : Request[_], requestParams: RequestParams) : Boolean = false

  def handlePost(request : Request[_], requestParams: RequestParams): Future[Either[Int, R]] =
    Future.successful(Left(500))

  def handlePut(request : Request[_], requestParams: RequestParams) : Future[Either[String, R]] =
    Future.successful(Left("Not implemented by resource"))

  def handleRequest(pathParams: Seq[PathParam]): EssentialAction = Action.async { request =>
  	// Bootstrap decision tree
  	handleAllowedMethods(request, pathParams)
  }

  def handleAllowedMethods(request : Request[_], pathParams: Seq[PathParam]): Future[Result] = {
  	allowedMethods.contains(request.method) match {
      case true  => handleBadRequest(request, pathParams)
      case false => Future.successful(Results.MethodNotAllowed)
    }
  }

  def extractRequestParams(request: Request[_], pathParams: Seq[PathParam]): Option[RequestParams]

  def handleBadRequest(request : Request[_], pathParams: Seq[PathParam]): Future[Result] = {
    extractRequestParams(request, pathParams) match {
      case Some(requestParams) => handleUnauthorized(request, requestParams)
      case None => Future.successful(BadRequest)
    }
  }

  def handleUnauthorized(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	isAuthorized(request, requestParams) match {
      case true  => handleForbidden(request, requestParams)
      case false => Future.successful(Results.Unauthorized)
    }
  }

  def handleForbidden(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	!isForbidden(request, requestParams) match {
      case true  => handleNotImplemented(request, requestParams)
      case false => Future.successful(Results.Forbidden)
    }
  }

  def handleNotImplemented(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	isImplemented(request.headers) match {
      case true  => handleUnsupportedMediaType(request, requestParams)
      case false => Future.successful(Results.NotImplemented)
    }
  }

  def handleUnsupportedMediaType(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	isContentTypeSupport(request.contentType) match {
      case true  => handleRequestEntityTooLarge(request, requestParams)
      case false => Future.successful(Results.UnsupportedMediaType)
    }
  }

  def handleRequestEntityTooLarge(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	!isRequestEntityTooLarge(request) match {
      case true  => handleMethod(request, requestParams)
      case false => Future.successful(Results.EntityTooLarge)
    }
  }

  def handleMethod(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	request.method match {
      case OPTIONS => Future.successful(Results.Ok.withHeaders("Allow" -> allowedMethods.mkString(", ")))
      case HEAD | GET => isResourceExists(request, requestParams) map {
        case Some(resource) => handleReads(request, requestParams, resource)
        case None => NotFound
      }
      case POST => handlePOST(request, requestParams)
      case PUT => handleWrites(request, requestParams)
    }
  }

  def handleResourceExists(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	//  from now on, we will follow an over simplified version of the header flow...because HACKATHON!
  	isResourceExists(request, requestParams) flatMap {
      case Some(r)  => Future.successful(handleReads(request, requestParams, r))
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

  def handleWrites(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	request.method == "PUT" match {
      case true  => handlePut(request, requestParams) map {
      	case Left(s) => Results.InternalServerError(s)
      	case Right(resource) => Results.Ok // add location header and deal with creation
      }
      case false => handleResourcePreviouslyExisted(request, requestParams)
    }
  }

  def handleResourcePreviouslyExisted(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	isResourcePreviouslyExisted(request, requestParams) match {
      case true  => handleResourceMovedPermanently(request, requestParams)
      case false => handlePOST(request, requestParams)
    }
  }

  def handlePOST(request : Request[_], requestParams: RequestParams): Future[Result] = {
  	request.method == "POST" match {
      case true  => handlePost(request, requestParams) map {
      	case Left(s) => Results.Status(s)
      	case Right(resource) =>
          Results.Ok // add location header and deal with creation
      }
      case false => Future.successful(Results.NotFound)
    }
  }

  def handleResourceMovedPermanently(request : Request[_], requestParams: RequestParams): Future[Result] = ???

  def extractPathParam(name: String, pathParams: Seq[PathParam]) =
    pathParams.find(_.name == name).map(_.value)

}