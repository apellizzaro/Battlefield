package model
import scala.math._

/**
  * Created by anton on 12/16/2016.
  */
case class BattleShip (startPosition: Point2D, endPosition: Point2D) {
  def length : Int = {
    val dx = endPosition.x - startPosition.x
    val dy = endPosition.y - startPosition.y
    if (dx==0)
      abs(dy) + 1
    else if (dy==0)
      abs(dx)+1
    else
      abs(dx)+1
  }

  def allCells : Seq[Point2D] = {
    if (startPosition==endPosition)
      List(startPosition)
    else {
      val dx = endPosition.x - startPosition.x
      val dy = endPosition.y - startPosition.y

      if (dx == 0)
        List.tabulate(abs(dy)+1)(n=>Point2D(startPosition.x, startPosition.y+n*(dy/abs(dy))))
      else if (dy == 0)
        List.tabulate(abs(dx)+1)(n=>Point2D(startPosition.x+n*(dx/abs(dx)), startPosition.y))
      else
        List.tabulate(abs(dx)+1)(n=>Point2D(startPosition.x+n*(dx/abs(dx)), startPosition.y+n*(dy/abs(dy))))
    }
  }
}
