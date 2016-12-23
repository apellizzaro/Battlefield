package module

import javax.inject._

import dataAccess.GameDaoInterface
import model._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by anton on 12/16/2016.
  */

class GameManager @Inject() (playerManager: PlayerManger, gameDataAccess: GameDaoInterface) (implicit exec: ExecutionContext){

  type ResultGameOperation = Future[String Either Game]

  def whosTurnIsIt(game:Game):String = game.players.head.name

  def startGame(gameId:String): ResultGameOperation = {
    retrieveGame(gameId).map {
      case Left(s) => Left(s)
      case Right(g) =>
        val players = playerManager.setupOpponentsBoards(g.players,g.boardSize)
        val newGame = Game (g.gameId,g.gameName,g.boardSize,g.shipsConfiguration,players,GameInProgress)
        gameDataAccess.saveGame(newGame)
        Right(newGame)
    }
  }

  def shoot (gameId: String, point2D: Point2D): Future [String Either (Game,Map[String,ResultShooting])] = {
    retrieveGame(gameId).map {
      case Left(s) => Left(s)
      case Right(g) => val (game, results) = playTurn(g, point2D)
        gameDataAccess.saveGame(game)
        Right((game, results))
    }
  }

  def getGameStatus (gameId:String): Future[ String  Either GameStatusResponse] = {
    retrieveGame(gameId) map {
      case Left(s) => Left (s)
      case Right (g) =>
        Right(GameStatusResponse(g.status.toString,g.shipsConfiguration, g.players.map(_.name),g.players.find(_.ownBoard.stillAlive).map(_.name).getOrElse("")))
    }
  }

  def playTurn(game:Game, shot:Point2D): (Game, Map [String, ResultShooting]) = {

    val (alivePlayers, deadPlayers) = game.players.partition (_.ownBoard.stillAlive)

    val (player, opponents) = playerManager.sendShotToPlayers(alivePlayers.head,shot,alivePlayers.tail)

    val opponentsPlayers = opponents.map(_._1)

    //check for winner => if in all player's board all battleship are hit
    val won = opponentsPlayers.forall( o => !o.ownBoard.stillAlive)

    (Game(game.gameId,game.gameName,game.boardSize,game.shipsConfiguration, (opponentsPlayers :+ player) ++ deadPlayers,if (won) GameEnded else game.status),
      opponents.map(t=> (t._1.name,t._2)).toMap)
  }

  def newGame (gameName: String, boardSize: Int, initialSetup: GameSetup): Future [String Either Game] = {
    GameValidator(gameName,boardSize,initialSetup) match {
      case Right (g) => gameDataAccess.saveGame(g).map {
        case true => Right(g)
        case _ => Left ("error saving the game")
      }
      case Left(e) => Future(Left("Could not start game because: " + e))
    }
  }

  def getNextPlayerTurn (gameId:String):Future[Option[Player]] = {
    retrieveGame(gameId).map {
      case Left(e) => None
      case Right(g) if g.status != GameInProgress => None
      case Right(g) => g.players.find(_.ownBoard.stillAlive)
    }
  }

  def addPlayers (gameId:String, playerSetup:Map[String,Seq[BattleShip]]):ResultGameOperation = {
    retrieveGame(gameId).map {
      case Left(s) => {
        println(s)
        Left(s)
      }
      case Right (g) if g.status != GameSettingUp => Left ("Game already in  progress")
      case Right(g) => {
        val newGame = addPlayers(g, playerSetup)
        gameDataAccess.saveGame(newGame)
        Right(newGame)
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

      Game(game.gameId,game.gameName,game.boardSize,game.shipsConfiguration, game.players ++ playersSetup, game.status)

    }
  }

  def retrieveGame (gameId:String): Future[String Either Game] = {
    gameDataAccess.retrieveGame(gameId)
  }
}
