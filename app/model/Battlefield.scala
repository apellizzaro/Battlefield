package model

import org.json4s.JsonAST.{JNull, JString}
import org.json4s.{CustomSerializer, DefaultFormats, Formats}

/**
  * Created by anton on 12/16/2016.
  */


case class playerBoardResult (ownBoard:BattleField, opponentsBoards: Map[String, BattleField])

case class BattleField (ships: Seq[BattleShip], grid:Grid) {
  def stillAlive:Boolean = grid.g.exists(_.contains(BattleShipSafe))
}


object BattleField {
  def apply(size: Int, ships: Seq[BattleShip]): BattleField = {
    val initialGrid: Grid = Grid(Array.tabulate(size, size)((x, y) => {
      ships.find(s => occupySpace(s,Point2D(x, y))).map(_ => BattleShipSafe).getOrElse(EmptySea)
    }))

    new BattleField(ships, initialGrid)
  }

  def occupySpace (s: BattleShip, p: Point2D): Boolean = {
    //http://stackoverflow.com/questions/11907947/how-to-check-if-a-point-lies-on-a-line-between-2-other-points
    val dxc = p.x - s.startPosition.x
    val dyc = p.y - s.startPosition.y

    val dxl = s.endPosition.x - s.startPosition.x
    val dyl = s.endPosition.y - s.startPosition.y

    if ( (dxc * dyl - dyc * dxl) == 0 ){
      if (Math.abs(dxl)>=Math.abs(dyl)){
        if (dxl>0)
          s.startPosition.x <= p.x && p.x <= s.endPosition.x
        else
          s.endPosition.x <= p.x && p.x <= s.startPosition.x
      }
      else {
        if (dyl >0)
          s.startPosition.y <= p.y && p.y <= s.endPosition.y
        else
          s.endPosition.y <= p.y && p.y <= s.startPosition.y
      }
    }
    else false
  }
}