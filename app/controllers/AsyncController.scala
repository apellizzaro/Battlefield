package controllers

import akka.actor.ActorSystem
import javax.inject._

import model._
import module.GameManager
import module.json.json4s.Json
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

@Singleton
class AsyncController @Inject()(actorSystem: ActorSystem, gameManager:GameManager)(implicit exec: ExecutionContext) extends Controller {

  def newGame (name: String, boardsize:Int)= Action.async (parse.json) {
    request=> {
      val bodyStr = request.body.toString()
      val initialSetup = Json.parse[Map[String,List[BattleShip]]] (bodyStr)

      val newgame=gameManager.newGame(name,boardsize,initialSetup)

      Future(Ok(newgame.gameId.toString))
    }

  }
  def  addPlayers  (gameId:String) = Action.async (parse.json) {
    request => {
      val bodyStr = request.body.toString()
      val initialSetup = Json.parse[Map[String, List[BattleShip]]](bodyStr)
      gameManager.addPlayers(gameId,initialSetup ).map {
        case Left(s) => NotFound(s)
        case Right(game) => Ok(game.gameId.toString)
        }
    }
  }

  def getNextPlayerTurn(gameId:String)= Action.async {
    gameManager.getNextPlayerTurn (gameId).map {
      op => op.map(p => Ok(p.name)).getOrElse(NotFound("Player not found"))
    }
  }

  def startGame (gameId:String) = Action.async  {
    request => {
      gameManager.startGame(gameId).map {
        case Left (e) => NotFound(e)
        case Right(g) => Ok(g.gameId)
      }
    }
  }

  def gameStatus  (gameId:String):Action[AnyContent] = Action.async {
    gameManager.getGameStatus (gameId) map {
      case Left(e) => NotFound(e)
      case Right(s) => Ok(Json.generate(s))
    }
  }

  def shoot  (gameId:String) = Action.async (parse.json) {
    request => {
      val shot = Json.parse[Point2D](request.body.toString())
      gameManager.shoot(gameId, shot).map {
        case Left(s) => NotFound(s)
        case Right(rr) =>
          Ok(Json.generate(rr._2))
      }
    }
  }

  def getPlayerBoards (gameId:String,playerId:String):Action[AnyContent] = Action.async  {
    gameManager.retrieveGame(gameId).map {
      case Left(s) => NotFound(s)
      case Right(g)=>g.players.find(_.name==playerId).map {
          p=> Ok(Json.generate(playerBoardResult(p.ownBoard,p.opponentsBoards)))
        }.getOrElse(NotFound("Player not  found"))
    }
  }

}
