package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.example.domain.Position.{down, up}

package object domain {

  case class CellId(value: String) extends AnyVal

  case class Cell(
    cellId: CellId,
    title: String,
    north: Option[CellId] = None,
    south: Option[CellId] = None,
    east: Option[CellId] = None,
    west: Option[CellId] = None,
    switches: List[SwitchId] = Nil
  )

  object Cell {
    import Direction._
    def neighbours(cell: Cell): List[(Direction, CellId)] = {
      val all = List(north -> cell.north, south -> cell.south, west -> cell.west, east -> cell.east)
      all collect { case (direction, Some(neighbour)) => direction -> neighbour }
    }
  }

  sealed trait Direction
  object Direction {
    case object north extends Direction
    case object south extends Direction
    case object east extends Direction
    case object west extends Direction
  }

  case class SwitchId(value: String) extends AnyVal

  case class Switch(
    switchId: SwitchId,
    position: Position
  ) {
    def flip(): Switch = {
      if (position == up) copy(position = down)
      else copy(position = up)
    }
  }

  sealed trait Position
  object Position {
    case object up extends Position
    case object down extends Position
  }

}
