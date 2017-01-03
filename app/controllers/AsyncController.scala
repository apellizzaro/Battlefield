package controllers

import akka.actor.ActorSystem
import javax.inject._

import model._
import module.{GameManager, UserManager}
import module.json.json4s.Json
import play.api._
import play.api.http.Status._
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._


@Singleton
class AsyncController @Inject()(actorSystem: ActorSystem, gameManager:GameManager,userManager: UserManager)(implicit exec: ExecutionContext) extends Controller {


  object Authenticated extends AuthenticatedBuilder (req => {
    req.headers.get("x-battlefield-userToken").map (t=> userManager.geUserByToken(t))},
    req=>{Unauthorized("User unauthorized")})



  def newGame (name: String, boardsize:Int)= Authenticated.async (parse.json) {
    request => {
      request.user.flatMap {
        case Left(e) => Future(Unauthorized(e))
        case Right(usr) =>  val bodyStr = request.body.toString()
          val initialSetup = Json.parse[GameSetup] (bodyStr)

          gameManager.newGame(name,boardsize,usr.name,initialSetup).map  {
            case Right (g) => Ok(g.gameId)
            case Left (e) => BadRequest (e)
          }
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

  def startGame (gameId:String) = Authenticated.async (request => {
    request.user.flatMap {
      case Right (u) =>  gameManager.startGame(gameId,u).map {
        case Left(e) => NotFound(e)
        case Right(g) => Ok(g.gameId)
      }
      case Left (e) => Future(Unauthorized(e))
    }
  })

  def gamesSummary = Action.async {
    gameManager.getGamesSummary.map {
      case Right (gs) => Ok (Json.generate(gs))
      case Left (e) => InternalServerError(e)
    }
  }

  def gameDetails (gameId: String) = Action.async {
    gameManager.getGame (gameId).map {
      case Left(e) => NotFound(e)
      case Right(g) => Ok(Json.generate(Game (g.gameId,g.gameName,g.boardSize,g.ownerName,g.shipsConfiguration,g.players.map(p=>Player(p.name,BattleField(0,List()),Map())),g.status)))
    }
  }

  def shoot  (gameId:String) = Authenticated.async (parse.json) { request =>
    request.user.flatMap {
      case Right(usr) => gameManager.shoot(gameId, usr,Json.parse[Point2D](request.body.toString())) map {
        case Right(r) => Ok(Json.generate(r._2))
        case Left(err) => NotFound(err)
      }
      case Left (e) => Future (Unauthorized(e))
    }
  }

  def getPlayerBoards (gameId:String):Action[AnyContent] = Authenticated.async  {request =>
    request.user.flatMap {
      case Right (usr) => gameManager.retrieveGame(gameId).map {
        case Left(s) => NotFound(s)
        case Right (g) => g.players.find(_.name==usr.name).map {
          p=>Ok(Json.generate(playerBoardResult(p.ownBoard,p.opponentsBoards)))
        }.getOrElse(NotFound("Player not  found"))
        case Left(e) => NotFound(e)
      }
      case Left(e) => Future (Unauthorized("User unauthorized"))
    }
  }

}
