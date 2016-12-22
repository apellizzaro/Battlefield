package module

import javax.inject.Inject

import model._

/**
  * Created by anton on 12/16/2016.
  */
class BattleFieldManger @Inject () () {

  def receiveShot (battleField:BattleField, shot: Point2D): (BattleField,ResultShooting) = {
    val boardSize = battleField.grid.g.length
    val newGrid:Grid = Grid(Array.tabulate(boardSize, boardSize)((x, y) => {
      if (x != shot.x || y != shot.y)
        battleField.grid(x,y)
      else if (battleField.grid(shot) == EmptySea)
        MissedShot
      else if (battleField.grid(shot) == BattleShipSafe)
        BattleShipHit
      else
        battleField.grid(shot)
    }))

    //check if we lost:
    val weLost = !battleField.stillAlive

    val resultShoot: ResultShooting = if (weLost) AllSunken
    else
      battleField.grid(shot.x, shot.y) match {
        case EmptySea => Missed
        case MissedShot => Missed
        case BattleShipHit => AlreadyTaken
        case BattleShipSafe => if (checkSunken(battleField,shot)) Sunk else Hit
        case _ => Missed
      }

    (BattleField(battleField.ships, newGrid), resultShoot)
  }

  def checkSunken (battleField:BattleField,point2D: Point2D):Boolean = {

    def visitPoint (p:Point2D, alreadyVisited:Seq[Point2D]): Option[(Point2D,SeaStatus)] = {
      if (p>=Point2D(0,0)  && p < Point2D (battleField.grid.g.length, battleField.grid.g.length) && !alreadyVisited.contains(p)) {
        Some((p, battleField.grid(p.x, p.y)))
      }
      else
        None
    }

    def getShipStatus (pp: Point2D, acc: Seq[(Point2D,SeaStatus)], alreadyVisited:Seq[Point2D]): Seq[(Point2D,SeaStatus)] = {
      val pointsVisited = for (dx <- Range(-1,2);
                              dy <- Range(-1,2))
        yield {
          visitPoint(Point2D(pp.x+dx,pp.y+dy),alreadyVisited)
        }

      if (pointsVisited.forall(vp => vp.isEmpty ||  vp.get._2==EmptySea || vp.get._2==MissedShot))
        acc
      else {
        val pointsOfShip = pointsVisited.filter(vp => vp.isDefined && vp.get._2!=EmptySea && vp.get._2!=MissedShot).map(_.get)
        pointsOfShip.flatMap(p => getShipStatus(p._1, acc :+ p, alreadyVisited ++ pointsVisited.filter(_.isDefined).map(_.get._1)))
      }
    }

    val shipStatus = getShipStatus(point2D,List((point2D,BattleShipHit)), List(point2D))

    shipStatus.forall(_._2 == BattleShipHit)
  }

  def newBattleFiledAfterShooting (battleField: BattleField, shot: Point2D, shotRes: ResultShooting) : BattleField = {

    val gridSize =battleField.grid.g.length
    val updatedGrid:Grid = Grid(Array.tabulate(gridSize,gridSize)( (x,y)=> if(Point2D(x,y)==shot) mapRestoSeaStatus(shotRes) else battleField.grid(x,y)))

    BattleField (battleField.ships,updatedGrid)
  }

  def mapRestoSeaStatus (res:ResultShooting): SeaStatus = {
    res match {
      case Sunk => BattleShipHit
      case AlreadyTaken => BattleShipHit
      case Hit => BattleShipHit
      case _ => MissedShot
    }
  }
}
