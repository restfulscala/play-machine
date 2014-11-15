package org.restfulscala.playmachine.example

import play.api.mvc.{Action, Controller}

object StandardController extends Controller {

  def handle() = Action { req =>
    Ok("foo")
  }

}
