package dataAccess

import model._
import javax.inject._

import com.google.inject.ImplementedBy
import module.json.json4s.Json
import storer.StoreApi

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GameDao])
trait GameDaoInterface {
  def saveGame (game:Game): Future[Boolean]
  def retrieveGame (gameId:String): Future[Either[String,Game]]
  def getGamesSummary : Future [String Either List[Game]]
}
/**
  * DAO for Games. Games are store in Json format
  */
@Singleton
class GameDao @Inject()(storer:StoreApi)(implicit exec: ExecutionContext) extends GameDaoInterface{

  def saveGame (game:Game): Future[Boolean] = {
    val jstring = Json.generate (game)

    storer.saveObject(game.gameId.toString,jstring)
  }

  def retrieveGame (gameId:String): Future[Either[String,Game]] = {
     storer.retrieveObject(gameId).map {
        case Right (jstring) => {
          val game =Json.parse[Game](jstring)
          Right(game)
        }
        case Left(s) => Left(s)
    }
  }

  def getGamesSummary : Future [String Either List[Game]] = {
    storer.getGamesSummary.map {
      case Right(gs) =>
        Right(gs.map(g=>Json.parse[Game](g)))
      case Left(e) => Left (e)
    }
  }

}
