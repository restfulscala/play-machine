package org.restfulscala.playmachine.resource

import play.api.mvc.{Action, EssentialAction, Controller}

trait Resource extends Controller {

  def allowedMethods = Seq()

  def handleRequest(pathParams: Seq[PathParam]): EssentialAction = Action { request =>
    Ok("foo")
  }

}
