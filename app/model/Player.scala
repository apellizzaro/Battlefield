package model

/**
  * Created by anton on 12/16/2016.
  */

trait PlayerValidator {
  def validatePlayerSetup (g:Game, playerSetup:Map[String,Seq[BattleShip]]): Option[String] = {
    validatePlayerSetup (g.boardSize, g.shipsConfiguration,playerSetup)
  }

  def validatePlayerSetup (boardSize:Int, shipsConfiguration: BattleshipConfiguration, playerSetup:Map[String,Seq[BattleShip]]): Option[String] = {
    //are the ships inside the grid?
    val allInside = playerSetup.values.forall(b => b.forall(bb => bb.startPosition >= Point2D(0, 0) &&
      bb.endPosition >= Point2D(0, 0) &&
      bb.startPosition < Point2D(boardSize, boardSize) &&
      bb.endPosition < Point2D(boardSize, boardSize)))

    //we should verify that the ships do not intersect each other
    //that's a bit more complicated issue

    //verify that the ships in player configuration
    //are matching the gameConfiguration
    val setupConformsToSpec = playerSetup.values.forall(lb=> {
      //this is a Map of battleshipLeght -> How many are of those
      //this needs to be equal to BattleshipConfiguration
      val shipLengths = lb.groupBy(_.length).mapValues(_.length)
      val configToMap = shipsConfiguration.config.groupBy(_.length).mapValues(_.head.quantity)
      val sameKeys = (shipLengths -- configToMap.keys).isEmpty && (configToMap -- shipLengths.keys).isEmpty
      sameKeys && shipLengths.forall(kv => configToMap.get(kv._1).contains(kv._2))
    })

    if (!allInside)
      Some("Ships must be all inside the board")
    else if (!setupConformsToSpec)
      Some ("number and type of ships do not conform with game specification")
    else
      None
  }
}

/*A Player represented as his name, his player board and the opponets' boards*/
case class Player  (name: String, ownBoard: BattleField, opponentsBoards:Map[String, BattleField]  )
