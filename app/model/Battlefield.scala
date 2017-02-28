package model

import org.json4s.JsonAST.{JNull, JString}
import org.json4s.{CustomSerializer, DefaultFormats, Formats}

/**
  * Created by anton on 12/16/2016.
  */


/*
*Represent a Player game set: his own Battelfield, and the battlefields of his opponents, mapped with their name*/
case class playerBoardResult (ownBoard:BattleField, opponentsBoards: Map[String, BattleField])

/*
* Represents a player's battlefield: a list of Battleships, and a grid, representing the "sea"*/
case class BattleField (ships: Seq[BattleShip], grid:Grid) {
  def stillAlive:Boolean = grid.g.exists(_.contains(BattleShipSafe))
}

object BattleFieldWithValidation {
  def apply (size: Int, ships: Seq[BattleShip]) : String Either BattleField = {
    //check if ships are all inside
    if (ships.exists(b => b.startPosition < Point2D(0,0) || b.endPosition<Point2D(0,0) ||
        b.startPosition > Point2D(size,size) || b.endPosition>Point2D(size,size))) {
      Left("All ships must be inside the playing board")
    }
    else {
      Right (BattleField(size,ships))
    }
  }
}

object BattleField {
  def apply(size: Int, ships: Seq[BattleShip]): BattleField = {
    val allSpaces = ships.flatMap (_.allCells)
    val initialGrid: Grid = Grid(Array.tabulate(size, size)((x, y) => {
      allSpaces.find(_==Point2D(x,y)).map(_ => BattleShipSafe).getOrElse(EmptySea)
    }))

    BattleField(ships, initialGrid)
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