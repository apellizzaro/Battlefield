package model

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
  * Created by anton on 12/16/2016.
  */

sealed trait GameStatus
case object GameSettingUp extends GameStatus
case object GameInProgress extends GameStatus
case object GameEnded extends GameStatus


object GameStatusSerializer extends CustomSerializer[GameStatus](format => (
  {
    case JString(gameStatus) =>  gameStatus match {
      case "GameSettingUp" => GameSettingUp
      case "GameInProgress" => GameInProgress
      case "GameEnded" => GameEnded
    }
    case JNull => null
  },
  {
    case gameStatus:GameStatus => JString(gameStatus.getClass.getSimpleName.replace("$",""))
  }))

case class GameStatusResponse (status: String, players:Seq[String], nextTurn:String)

case class Game (gameId:String, gameName:String, boardSize:Int, players: Seq[Player], status:GameStatus )

object  Game {
  def apply(gameName: String, boardSize: Int, initialSetup: Map[String, List[BattleShip]]): Game = {
    val playersSetup = initialSetup.map { kv =>
      val curPlayerBattleField = BattleField(boardSize, kv._2)
      val opponentsBfs = initialSetup.filter(p => p._1 != kv._1).mapValues(_ => BattleField(boardSize, List()))
      Player(kv._1, curPlayerBattleField, opponentsBfs)
    }.toList
    Game(java.util.UUID.randomUUID().toString, gameName,boardSize, playersSetup, GameSettingUp)
  }
}

