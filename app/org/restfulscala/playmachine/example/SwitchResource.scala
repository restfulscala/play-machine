package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.resource.Resource

object SwitchResource extends Resource {
  override def allowedMethods = Set("HEAD", "GET", "OPTIONS")
}
