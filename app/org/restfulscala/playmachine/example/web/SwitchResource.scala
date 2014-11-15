package org.restfulscala.playmachine.example.web

import com.yetu.siren.Siren
import org.restfulscala.playmachine.example.domain.{Switch, SwitchId, SwitchRepository}
import org.restfulscala.playmachine.resource.{PathParam, Resource}
import play.api.mvc.Request
import org.restfulscala.playsiren._
import SirenRepresentations._
import play.api.libs.concurrent.Execution.defaultContext

object SwitchResource extends Resource[Switch, SwitchId] {

  override def allowedMethods = Set(HEAD, GET, POST, OPTIONS)

  override def extractRequestParams(request: Request[_], pathParams: Seq[PathParam]) = {
    extractPathParam("switchId", pathParams) map SwitchId
  }

  override def isResourceExists(request: Request[_], switchId: SwitchId) = SwitchRepository findById switchId

  override def handleGet(resource: Switch) = {
    case Accepts.Html()     => Ok(views.html.switch(resource))
    case AcceptsSirenJson() => Ok(Siren.asRootEntity(resource))
  }

  override def handlePost(request: Request[_], switchId: SwitchId) = isResourceExists(request, switchId) map {
      case Some(switch) =>
        val updated = switch.flip()
        SwitchRepository.save(updated)
        Right(updated)
      case None => Left(404)
    }

  override implicit def executionContext = defaultContext
}

