package org.restfulscala.playmachine.example

import play.api.GlobalSettings
import play.api.mvc.RequestHeader

object Global extends GlobalSettings {
  override def onRouteRequest(request: RequestHeader) = {
    MyDispatcher(request)
  }
}
