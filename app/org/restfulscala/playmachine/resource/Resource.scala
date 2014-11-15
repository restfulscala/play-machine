package org.restfulscala.playmachine.resource

import play.api.mvc.{EssentialAction, Controller}

trait Resource extends Controller {

  def handleRequest(): EssentialAction

}
