package model

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
  * Created by apellizz on 12/22/16.
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
