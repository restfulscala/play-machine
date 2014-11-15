package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.dispatcher.Dispatcher
import org.restfulscala.playmachine.dispatcher.PathMatch.{Param, Seg}
import org.restfulscala.playmachine.dispatcher.Route._
import org.restfulscala.playmachine.dispatcher.PathMatch._
import org.restfulscala.playmachine.example.web.{SwitchResource, CellResource}
import play.api.GlobalSettings
import play.api.mvc.RequestHeader

object Global extends GlobalSettings {

  val cells    =  "cells" / Param("cellId")       :~> CellResource
  val switches =  "switches" / Param("switchId")  :~> SwitchResource

  val myDispatcher = Dispatcher.from(cells, switches)

  override def onRouteRequest(request: RequestHeader) = {
    myDispatcher(request)
  }
}
