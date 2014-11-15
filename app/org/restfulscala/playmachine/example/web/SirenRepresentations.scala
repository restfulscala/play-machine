package org.restfulscala.playmachine.example.web

import com.yetu.siren.SirenRootEntityWriter
import com.yetu.siren.model.Entity.RootEntity
import com.yetu.siren.model.Property
import com.yetu.siren.model.Property.StringValue
import org.restfulscala.playmachine.example.domain.Cell

object SirenRepresentations {

  implicit val cellWriter = new SirenRootEntityWriter[Cell] {
    override def toSiren(cell: Cell): RootEntity = {
      RootEntity(
        classes = Some(List("cell")),
        properties = Some(List(
          Property("title", StringValue(cell.title))
        ))
      )
    }
  }

}
