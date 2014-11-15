package org.restfulscala.playmachine.example.web

import com.yetu.siren.SirenRootEntityWriter
import com.yetu.siren.model.Entity.{EmbeddedRepresentation, RootEntity}
import com.yetu.siren.model.Property.StringValue
import com.yetu.siren.model.{Action, Link, Property}
import org.restfulscala.playmachine.example.domain.{Cell, CellId, Switch}

object SirenRepresentations {

  implicit val cellWriter = new SirenRootEntityWriter[Cell] {
    override def toSiren(cell: Cell): RootEntity = {
      RootEntity(
        classes = Some(List("cell")),
        properties = Some(List(
          Property("title", StringValue(cell.title))
        )),
        links = Some(links(cell)),
        entities = Some(cell.switches map embeddedSwitchEntity)
      )
    }

    private def links(cell: Cell): List[Link] = {
      val current = link("current", cell.cellId)
      val neighbours = Cell.neighbours(cell) map { case (direction, cellId) => link(direction.toString, cellId)}
      current :: neighbours
    }

    private def link(rel: String, cellId: CellId): Link =
      Link(rel = List(rel), s"/cells/${cellId.value}")

    private def embeddedSwitchEntity(switch: Switch): EmbeddedRepresentation = {
      EmbeddedRepresentation(
        classes = Some(List("switch")),
        rel = List("item"),
        properties = Some(List(Property("position", StringValue(switch.position.toString)))),
        actions = Some(List(
          Action(
            name = "flip",
            href = s"/switches/${switch.switchId.value}",
            method = Some(Action.Method.POST)
          )
        ))
      )
    }

  }

}
