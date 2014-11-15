package org.restfulscala.playmachine.example

import org.restfulscala.playmachine.dispatcher.PathMatch.{Empty, Seg}
import org.restfulscala.playmachine.dispatcher.{PathMatch, Route, Dispatcher}

object MyDispatcher extends Dispatcher(
  List(Route(PathMatch(Seg("") :: Nil), CellResource))
)
