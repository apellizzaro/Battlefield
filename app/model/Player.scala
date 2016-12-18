package model

/**
  * Created by anton on 12/16/2016.
  */

case class Player  (name: String, ownBoard: BattleField, opponentsBoards:Map[String, BattleField]  )
