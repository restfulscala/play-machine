package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.resource.Resource

import play.api.mvc.Request
import play.api.http.Writeable
import org.restfulscala.playmachine.resource._

object CellResource extends Resource[String] {
	implicit override def writer: play.api.http.Writeable[String] = ???

	override def isResourceExists(request : Request[_], pathParams: Seq[PathParam]) = Some("I am a Cell!")
}
