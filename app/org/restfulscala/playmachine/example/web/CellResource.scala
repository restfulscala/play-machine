package org.restfulscala.playmachine.example.web

import com.yetu.siren.Siren
import org.restfulscala.playmachine.example.domain.{Cell, CellId, CellRepository}
import org.restfulscala.playmachine.example.web.SirenRepresentations._
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import org.restfulscala.playsiren._
import play.api.mvc.Request

object CellResource extends Resource[Cell, CellId] {

  override def allowedMethods = Set(HEAD, GET, OPTIONS)

  override def extractRequestParams(request: Request[_], pathParams: Seq[PathParam]) =
    pathParams.find(_.name == "cellId").map(p => CellId(p.value))

  override def isResourceExists(request: Request[_], cellId: CellId) =
      CellRepository findById cellId

  override def handleGet(resource: Cell) = {
      case AcceptsSirenJson() => Ok(Siren.asRootEntity(resource))
    }

}
