package org.restfulscala.playmachine.example.web

import com.yetu.siren.Siren
import org.restfulscala.playmachine.example.domain.{Switch, SwitchId, SwitchRepository}
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.mvc.Request
import org.restfulscala.playsiren._
import SirenRepresentations._

object SwitchResource extends Resource[Switch] {

  override def allowedMethods = Set(HEAD, GET, POST, OPTIONS)

  override def isResourceExists(request: Request[_], pathParams: Seq[PathParam]) = {
    pathParams.find(_.name == "switchId") flatMap { switchId =>
      SwitchRepository.findById(SwitchId(switchId.value))
    }
  }

  override def handleGet(resource: Switch) = {
    case AcceptsSirenJson() => Ok(Siren.asRootEntity(resource))
  }

  override def handlePost(request: Request[_], pathParams: Seq[PathParam]) = {
    isResourceExists(request, pathParams) match {
      case Some(switch) =>
        val updated = switch.flip()
        SwitchRepository.save(updated)
        Right(updated)
      case None => Left(404)
    }
  }

}

