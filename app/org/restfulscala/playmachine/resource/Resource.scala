package org.restfulscala.playmachine.resource

import play.api.mvc.{EssentialAction, Controller}
import shapeless.HList

trait Resource[PathParams <: HList] extends Controller {

  def handleRequest(pathParams: PathParams): EssentialAction

}
