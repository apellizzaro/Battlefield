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

  def newGame (name: String, boardsize:Int, owner:String)= Action.async (parse.json) {
    request=> {
      val bodyStr = request.body.toString()
      val initialSetup = Json.parse[GameSetup] (bodyStr)

      gameManager.newGame(name,boardsize,owner,initialSetup).map  {
        case Right (g) => Ok(g.gameId)
        case Left (e) => BadRequest (e)
      }
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

  def gameStats  = Action.async {
    gameManager.getGameStats.map {gs=>
      Ok(Json.generate(gs))
    }
  }

  def gamesSummary = Action.async {
    gameManager.getGamesSummary.map {
      case Right (gs) => Ok (Json.generate(gs))
      case Left (e) => InternalServerError(e)
    }
  }

  def gameStatus  (gameId:String):Action[AnyContent] = Action.async {
    gameManager.getGameStatus (gameId) map {
      case Left(e) => NotFound(e)
      case Right(s) => Ok(Json.generate(s))
    }
  }

  def gameDetails (gameId: String) = Action.async {
    gameManager.getGame (gameId).map {
      case Left(e) => NotFound(e)
      case Right(g) => Ok(Json.generate(Game (g.gameId,g.gameName,g.boardSize,g.ownerName,g.shipsConfiguration,List(),g.status)))
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
