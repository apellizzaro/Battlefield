package model

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
  * Created by apellizz on 12/22/16.
  */

trait ResultShooting
case object Hit extends ResultShooting
case object Missed extends ResultShooting
case object AlreadyTaken extends ResultShooting
case object Sunk extends ResultShooting
case object AllSunken extends ResultShooting

object ResultShootingSerializer extends CustomSerializer[ResultShooting](format => (
  {
    case JString(resultShooting) =>  resultShooting match {
      case "Hit" => Hit
      case "Missed" => Missed
      case "AlreadyTaken" => AlreadyTaken
      case "Sunk" => Sunk
      case _ => Missed
    }
    case JNull => null
  },
  {
    case resultShooting:ResultShooting => JString(resultShooting.getClass.getSimpleName.replace("$",""))
  }))

