package org.restfulscala.playmachine.example.web

import com.yetu.siren.Siren
import org.restfulscala.playmachine.example.domain.{Cell, CellId, CellRepository}
import org.restfulscala.playmachine.example.web.SirenRepresentations._
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import org.restfulscala.playsiren._
import play.api.mvc.Request

object CellResource extends Resource[Cell] {

  override def allowedMethods = Set(HEAD, GET, OPTIONS)

  override def isResourceExists(request: Request[_], pathParams: Seq[PathParam]) = {
    pathParams.find(_.name == "cellId") flatMap { cellId =>
      CellRepository.findById(CellId(cellId.value))
    }
  }

  override def handleGet(resource: Cell) = {
      case AcceptsSirenJson() => Ok(Siren.asRootEntity(resource))
    }

}
