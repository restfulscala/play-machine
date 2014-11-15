package org.restfulscala.playmachine.example.web

import org.restfulscala.playmachine.example.domain.Switch
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.http.Writeable
import play.api.mvc.Request

object SwitchResource extends Resource[Switch] {

  override implicit def writer: Writeable[Switch] = ???

  override def isResourceExists(request: Request[_], pathParams: Seq[PathParam]) = ???
}
