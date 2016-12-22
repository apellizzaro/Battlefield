package model

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
  * Created by apellizz on 12/22/16.
  */

sealed trait SeaStatus
case object EmptySea extends SeaStatus
case object MissedShot extends SeaStatus
case object BattleShipSafe extends SeaStatus
case object BattleShipHit extends SeaStatus

case object SeaStatusSerializer extends CustomSerializer[SeaStatus](format => (
  {
    case JString(seastatus) =>  seastatus match {
      case "EmptySea" => EmptySea
      case "MissedShot" => MissedShot
      case "BattleShipSafe" => BattleShipSafe
      case "BattleShipHit" => BattleShipHit
      case _ => EmptySea
    }
    case JNull => null
  },
  {
    case seaStatus:SeaStatus => JString(seaStatus.getClass.getSimpleName.replace("$",""))
  }))

