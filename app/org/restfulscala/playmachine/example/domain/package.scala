package org.restfulscala.playmachine.example

package object domain {

  case class CellId(value: String) extends AnyVal

  case class Cell(
    cellId: CellId,
    title: String,
    north: Option[CellId] = None,
    south: Option[CellId] = None,
    east: Option[CellId] = None,
    west: Option[CellId] = None,
    switches: List[Switch] = Nil
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
  )

  sealed trait Position
  object Position {
    case object Up extends Position
    case object Down extends Position
  }

}
