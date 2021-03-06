package model

/**
  * Created by apellizz on 12/22/16.
  */
/*Represents the "sea"
*/
case class Grid (g:Array[Array[SeaStatus]]) {
  def apply (x:Int,y:Int):SeaStatus = g(x)(y)

  def apply(p:Point2D): SeaStatus = g(p.x)(p.y)

  def size = g.length

}
