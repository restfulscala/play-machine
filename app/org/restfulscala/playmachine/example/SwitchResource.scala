package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.resource.Resource

import play.api.mvc.Request
import play.api.http.Writeable
import org.restfulscala.playmachine.resource._

object SwitchResource extends Resource[String] {
  
  override def allowedMethods = Set("HEAD", "GET", "OPTIONS")
  implicit override def writer: play.api.http.Writeable[String] = ???
  override def getResource(request : Request[_], pathParams: Seq[PathParam]) = "I am a Switch!"
}
