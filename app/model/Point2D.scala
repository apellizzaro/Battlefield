package model

/**
  * Created by anton on 12/16/2016.
  */
case class Point2D (x:Int, y:Int) {
  def <  (p:Point2D): Boolean = x<p.x || y<p.y
  def >  (p:Point2D): Boolean = x>p.x || y>p.y
  def >= (p:Point2D): Boolean = x>=p.x || y>=p.y

  def == (p:Point2D): Boolean = x==p.x && y==p.y
}

