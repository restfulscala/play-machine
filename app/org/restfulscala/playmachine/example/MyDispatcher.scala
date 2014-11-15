package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.dispatcher.Dispatcher
import play.api.mvc.{Handler, RequestHeader}
import shapeless.HNil

object MyDispatcher extends Dispatcher {
  override def apply(reqHeader: RequestHeader): Option[Handler] =
    Some(StandardController.handleRequest(HNil))
}
