package org.restfulscala.playmachine.example.domain

object CellRepository {

  val cells = Map(
    CellId("M") -> Cell(
      CellId("M"),
      "The Hall of Doom",
      west = Some(CellId("L")),
      east = Some(CellId("N"))
    ),
    CellId("L") -> Cell(
      CellId("L"),
      "Cell L",
      east = Some(CellId("M"))
    ),
    CellId("N") -> Cell(
      CellId("N"),
      "Cell N",
      west = Some(CellId("M")),
      north = Some(CellId("I"))
    ),
    CellId("I") -> Cell(
      CellId("I"),
      "Cell I",
      south = Some(CellId("N"))
    )

  )

  def findById(id: CellId): Option[Cell] = cells get id

}
