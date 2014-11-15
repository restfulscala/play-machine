package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.dispatcher.Dispatcher
import play.api.mvc.{Handler, RequestHeader}

object MyDispatcher extends Dispatcher {
  override def apply(reqHeader: RequestHeader): Option[Handler] = Some(StandardController.handle())
}
