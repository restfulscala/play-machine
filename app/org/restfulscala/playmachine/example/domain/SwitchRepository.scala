package org.restfulscala.playmachine.example.domain

import scala.concurrent.stm.Ref

object SwitchRepository {

  val switches = Ref(Map(
    SwitchId("1") -> Switch(SwitchId("1"), Position.down)
  ))

  def findById(switchId: SwitchId): Option[Switch] = switches.single.get.get(switchId)
  def save(switch: Switch) = {
    switches.single.transform(_.updated(switch.switchId, switch))
  }

}
