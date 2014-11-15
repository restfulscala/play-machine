package org.restfulscala.playmachine.example.web

import org.restfulscala.playmachine.example.domain.Cell
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.http.Writeable
import play.api.mvc.Request

object CellResource extends Resource[Cell] {

  override implicit def writer: Writeable[Cell] = ???

  override def isResourceExists(request: Request[_], pathParams: Seq[PathParam]) = {
    ???
  }
}
