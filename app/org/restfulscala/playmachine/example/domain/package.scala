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
    def neighbours(cell: Cell): List[(Direction, CellId)] = List()
  }

  sealed trait Direction
  object Direction {
    case object north extends Direction
    case object south extends Direction
    case object east extends Direction
    case object west extends Direction
  }

  case class Switch()

}
