package model

/**
  * Created by anton on 12/16/2016.
  */

/*Represent a configuration of a ship in the game: You have 3 ships of length 2*/
case class BattleshipModel (length:Int, quantity:Int)
/*The complete battleship configuration*/
case class BattleshipConfiguration (config: Seq[BattleshipModel])

/*The respose for a game status: includes the configuration and all the players*/
case class GameStatusResponse (status: String, configuration:BattleshipConfiguration, players:Seq[String], nextTurn:String)

/*A game setup: ships configuration, and a list of players's battleship (player name -> his list of Battleship*/
case class GameSetup (shipsConfiguration: BattleshipConfiguration, playersSetup:Map[String, List[BattleShip]])

/*a Game: id, name, size of the board, ships configuration, a list of players and the game status*/
case class Game (gameId:String, gameName:String, boardSize:Int, ownerName:String, shipsConfiguration: BattleshipConfiguration, players: Seq[Player], status:GameStatus )

object GameValidator extends PlayerValidator {
  def apply(gameName: String, boardsize:Int, owner:String, setup: GameSetup): String Either Game = {
    //are there enough spaces?
    val totalShipSpaces = setup.shipsConfiguration.config.foldLeft(0)((a, b) => a + b.length * b.quantity)

    val playerValidation = validatePlayerSetup(boardsize, setup.shipsConfiguration, setup.playersSetup)


    if (totalShipSpaces > boardsize * boardsize)
      Left("Not enough space on the board")
    else if (boardsize <= 2)
      Left("Board not big enough")
    else {
      playerValidation.map(s => Left(s)).getOrElse {
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
            case Right(ps) => ps
          }
          Right(Game(java.util.UUID.randomUUID().toString, gameName, boardsize,owner, setup.shipsConfiguration, playerRightSetup, GameSettingUp))
        }
      }
    }
  }
}

object  Game {
  def apply(gameName: String, boardSize: Int,  owner:String, gameSetup: GameSetup): Game = {
    val playersSetup = gameSetup.playersSetup.map { kv =>
      val curPlayerBattleField = BattleField(boardSize, kv._2)
      Player(kv._1, curPlayerBattleField, Map())
    }.toSeq

    Game(java.util.UUID.randomUUID().toString, gameName,boardSize,owner, gameSetup.shipsConfiguration, playersSetup, GameSettingUp)
  }
}

