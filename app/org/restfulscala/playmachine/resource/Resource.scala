package org.restfulscala.playmachine.resource

import play.api.mvc.{Action, EssentialAction, Controller}
import shapeless.HList

trait Resource[PathParams <: HList] extends Controller {

  def allowedMethods = Seq()

  def handleRequest(pathParams: PathParams): EssentialAction = Action { request =>
    Ok("foo")
  }

}
