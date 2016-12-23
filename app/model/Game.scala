package model

/**
  * Created by anton on 12/16/2016.
  */


case class BattleshipModel (length:Int, quantity:Int)
case class BattleshipConfiguration (config: Seq[BattleshipModel])

case class GameStatusResponse (status: String, configuration:BattleshipConfiguration, players:Seq[String], nextTurn:String)

case class GameSetup (shipsConfiguration: BattleshipConfiguration, playersSetup:Map[String, List[BattleShip]])

case class Game (gameId:String, gameName:String, boardSize:Int, shipsConfiguration: BattleshipConfiguration, players: Seq[Player], status:GameStatus )

object GameValidator {
  def apply(gameName: String,boardsize:Int, setup: GameSetup): String Either Game = {
    //are there enough spaces?
    val totalShipSpaces = setup.shipsConfiguration.config.foldLeft(0)((a, b) => a + b.length * b.quantity)

    //are the ships inside the grid?
    val allInside = setup.playersSetup.values.forall(b => b.forall(bb => bb.startPosition >= Point2D(0, 0) &&
      bb.endPosition >= Point2D(0, 0) &&
      bb.startPosition < Point2D(boardsize, boardsize) &&
      bb.endPosition < Point2D(boardsize, boardsize)))

    //we should verify that the ships do not intersect each other
    //that's a bit more complicated issue

    if (totalShipSpaces > boardsize * boardsize)
      Left("Not enough space on the board")
    else if (boardsize <= 2)
      Left("Board not big enough")
    else if (!allInside)
      Left("Ships must be all inside the board")
    else {

      val playersSetup = setup.playersSetup.map { kv =>
        BattleFieldWithValidation(boardsize, kv._2) match {
          case Right(bf) => Right(Player(kv._1, bf, Map()))
          case Left(e) => Left(e)
        }
      }.toList

      if (playersSetup.exists(_.isLeft)) {
        Left("error creating battlefield")
      }
      else {
        val playerRightSetup = playersSetup.map {
          case Right (ps)=> ps
        }
        Right(Game(java.util.UUID.randomUUID().toString, gameName,boardsize,setup.shipsConfiguration,playerRightSetup,GameSettingUp))
      }
    }
  }
}

object  Game {
  def apply(gameName: String, boardSize: Int,  gameSetup: GameSetup): Game = {
    val playersSetup = gameSetup.playersSetup.map { kv =>
      val curPlayerBattleField = BattleField(boardSize, kv._2)
      Player(kv._1, curPlayerBattleField, Map())
    }.toSeq

    Game(java.util.UUID.randomUUID().toString, gameName,boardSize, gameSetup.shipsConfiguration, playersSetup, GameSettingUp)
  }
}

