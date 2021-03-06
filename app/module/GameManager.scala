package module

import javax.inject._

import dataAccess.GameDaoInterface
import model._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Manages a game
  */

class GameManager @Inject() (playerManager: PlayerManger, gameDataAccess: GameDaoInterface) (implicit exec: ExecutionContext) extends PlayerValidator {

  type ResultGameOperation = Future[String Either Game]

  def whosTurnIsIt(game:Game):String = game.players.head.name

  //Set the status of a game as InProgress, only the player who created the game can start it
  //At this stage the players' opponents' board get initialized
  //Returns a new Game
  def startGame(gameId:String, user:User): ResultGameOperation = {
    retrieveGame(gameId).map {
      case Left(s) => Left(s)
      case Right(g) if g.ownerName!=user.name => Left ("Unauthorized User")
      case Right(g) =>
        val players = playerManager.setupOpponentsBoards(g.players,g.boardSize)
        val newGame = Game (g.gameId,g.gameName,g.boardSize,g.ownerName, g.shipsConfiguration,players,GameInProgress)
        gameDataAccess.saveGame(newGame)
        Right(newGame)
    }
  }

  /*
    Perfomer a shoot from a player.
    Checks that is the user's turn and that the game is not ended
    Returns left error or Right a tuple of new Gam and the result of the shooting, mapped with user name
   */
  def shoot (gameId: String, user:User, point2D: Point2D): Future [String Either (Game,Map[String,ResultShooting])] = {
    retrieveGame(gameId).map {
      case Left(s) => Left(s)
      case Right(g) if g.players.head.name != user.name => Left ("Not user turn")
      case Right(g) if g.status==GameEnded => Left ("Game Ended," + g.players.find(p=>p.ownBoard.stillAlive).map(_.name).getOrElse("Unknown") + " is the winner")
      case Right(g) => val (game, results) = playTurn(g, point2D)
        gameDataAccess.saveGame(game)
        Right((game, results))
    }
  }


  def getGamesSummary : Future[String Either List[GameSummary]] = {
    gameDataAccess.getGamesSummary.map {
      case Right (gs) => Right(gs.map { g=> GameSummary(g.gameName,g.gameId,g.ownerName, g.boardSize,g.status,g.players.length)})
      case Left (e) => Left (e)
    }
  }

  def getGame (gameId: String) : Future [String Either Game] = {
    gameDataAccess.retrieveGame(gameId)
  }

  //plays a tunr on hte game, returns a new Game and the result of the shooting
  def playTurn(game:Game, shot:Point2D): (Game, Map [String, ResultShooting]) = {

    //Partition the list of players to exclude the players that already lost
    val (alivePlayers, deadPlayers) = game.players.partition (_.ownBoard.stillAlive)

    //The player in the head ot Players is the one whose turn is to shoot
    //ask the player manager to send the shot for that player.
    // this will return a new Player with the updated boards and the new opponents
    val (player, opponents) = playerManager.sendShotToPlayers(alivePlayers.head,shot,alivePlayers.tail)

    //get only the opponents players
    val opponentsPlayers = opponents.map(_._1)

    //check for winner => if in all player's board all battleship are hit
    val won = opponentsPlayers.forall( o => !o.ownBoard.stillAlive)

    //create a new Game with the new status
    //notice that the player who just shoot will go to the end of the list of players,
    //the player's turn is the one in front of the list
    (Game(game.gameId,game.gameName,game.boardSize,game.ownerName, game.shipsConfiguration, (opponentsPlayers :+ player) ++ deadPlayers,if (won) GameEnded else game.status),
      opponents.map(t=> (t._1.name,t._2)).toMap)
  }

  def newGame (gameName: String, boardSize: Int, owner:String,initialSetup: GameSetup): Future [String Either Game] = {
    GameValidator(gameName,boardSize,owner,initialSetup) match {
      case Right (g) => gameDataAccess.saveGame(g).map {
        case true => Right(g)
        case _ => Left ("error saving the game")
      }
      case Left(e) => Future(Left("Could not start game because: " + e))
    }
  }

  def getNextPlayerTurn (gameId:String):Future[String Either Player] = {
    retrieveGame(gameId).map {
      case Left(e) => Left(e)
      case Right(g) if g.status == GameEnded => Left("Game has ended!")
      case Right(g) => Right(g.players.find(_.ownBoard.stillAlive).get)
    }
  }

  //Adds a player to the game, while the game is 'seeting up'
  def addPlayers (gameId:String, playerSetup:Map[String,Seq[BattleShip]]):ResultGameOperation = {
    retrieveGame(gameId).map {
      case Left(s) => {
        println(s)
        Left(s)
      }
      case Right (g) if g.status != GameSettingUp => Left ("Game already in  progress")
      case Right(g) => {
        validatePlayerSetup (g,playerSetup).map ( s=>Left(s)).getOrElse {
          val newGame = addPlayers(g, playerSetup)
          gameDataAccess.saveGame(newGame)
          Right(newGame)
        }
      }
    }
  }

  private def addPlayers (game:Game, playerInitialSetup: Map[String,Seq[BattleShip]]): Game = {
    if (game.status != GameSettingUp)
      game
    else {
      val playersSetup = playerInitialSetup.map { kv =>
        val curPlayerBattleField = BattleField(game.boardSize, kv._2)
        Player(kv._1, curPlayerBattleField, Map())
      }.toList

      //if players already exists, replace it
      val removedDuplicates = game.players.filterNot(p=>playersSetup.exists(_.name == p.name))

      Game(game.gameId,game.gameName,game.boardSize,game.ownerName, game.shipsConfiguration, removedDuplicates ++ playersSetup, game.status)

    }
  }

  def retrieveGame (gameId:String): Future[String Either Game] = {
    gameDataAccess.retrieveGame(gameId)
  }
}
