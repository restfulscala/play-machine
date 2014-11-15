package org.restfulscala.playmachine.dispatcher

import play.api.mvc.{RequestHeader, Handler}

trait Dispatcher extends (RequestHeader => Option[Handler])