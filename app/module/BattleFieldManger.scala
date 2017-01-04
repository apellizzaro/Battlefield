package module

import javax.inject.Inject

import model._

/**
  * Created by anton on 12/16/2016.
  */
class BattleFieldManger @Inject () () {

  def receiveShot (battleField:BattleField, shot: Point2D): (BattleField, ResultShooting, Seq[Point2D]) = {
    val boardSize = battleField.grid.size
    val sunkenPoints = if (battleField.grid(shot) == BattleShipSafe && checkSunken(battleField,shot)) {
      battleField.ships.find(s => s.allCells.contains(shot)).map(_.allCells).getOrElse(List())
    }
    else List()

    val newGrid:Grid = Grid(Array.tabulate(boardSize, boardSize)((x, y) => {
      if (x != shot.x || y != shot.y)
        battleField.grid(x,y)
      else if (battleField.grid(shot) == EmptySea)
        MissedShot
      else if (sunkenPoints.contains(Point2D(x,y)))
        BattleShipSunk
      else if (battleField.grid(shot) == BattleShipSafe)
        BattleShipHit
      else
        battleField.grid(shot)
    }))

    val returBattleField = BattleField(battleField.ships, newGrid)

    //check if we lost:
    val weLost = !returBattleField.stillAlive

    val resultShoot: ResultShooting = if (weLost) AllSunken
    else
      battleField.grid(shot.x, shot.y) match {
        case EmptySea => Missed
        case MissedShot => Missed
        case BattleShipHit => AlreadyTaken
        case BattleShipSafe => if (checkSunken(battleField,shot)) Sunk else Hit
        case _ => Missed
      }

    (returBattleField, resultShoot,sunkenPoints)
  }

  def checkSunken (battleField:BattleField,point2D: Point2D):Boolean = {
    battleField.ships.find(s => s.allCells.contains(point2D)). //find the ship that was hit
      exists(ac => {val allMycells  = ac.allCells
      allMycells.filterNot(_==point2D).forall( c => {
        val gridValue = battleField.grid(c.x,c.y)
        gridValue==BattleShipHit
      })
    })
  }

  def newBattleFiledAfterShooting (battleField: BattleField, shot: Point2D, shotRes: ResultShooting, sunkenPoints: Seq[Point2D]) : BattleField = {

    val gridSize =battleField.grid.size
    val updatedGrid:Grid = Grid(Array.tabulate(gridSize,gridSize)( (x,y)=>
      if (shotRes==Sunk && sunkenPoints.contains(Point2D(x,y)))
        BattleShipSunk
      else if(Point2D(x,y)==shot)
        mapRestoSeaStatus(shotRes)
      else
        battleField.grid(x,y)))

    BattleField (battleField.ships,updatedGrid)
  }

  def mapRestoSeaStatus (res:ResultShooting): SeaStatus = {
    res match {
      case Sunk => BattleShipSunk
      case AlreadyTaken => BattleShipHit
      case Hit => BattleShipHit
      case _ => MissedShot
    }
  }
}
