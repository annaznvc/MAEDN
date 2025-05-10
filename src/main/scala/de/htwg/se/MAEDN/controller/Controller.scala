package de.htwg.se.MAEDN.controller

import de.htwg.se.MAEDN.util.Observable
import de.htwg.se.MAEDN.model.Manager

class Controller extends Observable {
  var manager: Manager = Manager(this)
}
