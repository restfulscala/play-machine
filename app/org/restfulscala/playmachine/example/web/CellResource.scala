package org.restfulscala.playmachine.example.web

import com.yetu.siren.Siren
import org.restfulscala.playmachine.example.domain.{Cell, CellId, CellRepository}
import org.restfulscala.playmachine.example.web.SirenRepresentations._
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import org.restfulscala.playsiren._
import play.api.libs.concurrent.Execution._
import play.api.mvc.Request

object CellResource extends Resource[Cell, CellId] {

  override def allowedMethods = Set(HEAD, GET, OPTIONS)

  override def extractRequestParams(request: Request[_], pathParams: Seq[PathParam]) =
    extractPathParam("cellId", pathParams) map CellId

  override def isResourceExists(request: Request[_], cellId: CellId) = CellRepository findById cellId

  override def handleGet(resource: Cell) = {
    case Accepts.Html()     => Ok(views.html.cell(resource))
    case AcceptsSirenJson() => Ok(Siren.asRootEntity(resource))
    }

  override implicit def executionContext = defaultContext

}
