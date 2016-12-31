import model.{BattleShip, Point2D}
import org.scalatestplus.play.PlaySpec

/**
  * Created by apellizz on 12/24/16.
  */
class BattleShipSpec extends PlaySpec {

  "A BattleShip" must {
    "measure 1" in {
      BattleShip(Point2D(1,1),Point2D(1,1)).length mustBe 1
    }

    "measure 2" in {
      BattleShip(Point2D(1,1),Point2D(2,2)).length mustBe 2
      BattleShip(Point2D(1,1),Point2D(1,2)).length mustBe 2
      BattleShip(Point2D(1,1),Point2D(2,1)).length mustBe 2
    }

    "measure 3" in {
      BattleShip(Point2D(1,1),Point2D(3,3)).length mustBe 3
      BattleShip(Point2D(1,1),Point2D(1,3)).length mustBe 3
      BattleShip(Point2D(1,1),Point2D(3,1)).length mustBe 3
    }

    "return point in x" in {
      val pts = BattleShip(Point2D(1,1),Point2D(3,1)).allCells
      pts.length mustBe 3
      pts.head mustBe Point2D(1,1)
      pts.tail.head mustBe Point2D(2,1)
      pts.tail.tail.head mustBe Point2D(3,1)
    }

    "return point in negative x" in {
      val pts = BattleShip(Point2D(3,1),Point2D(1,1)).allCells
      pts.length mustBe 3
      pts.head mustBe Point2D(3,1)
      pts.tail.head mustBe Point2D(2,1)
      pts.tail.tail.head mustBe Point2D(1,1)
    }

    "return point in xy" in {
      val pts = BattleShip(Point2D(2,1),Point2D(4,3)).allCells
      pts.length mustBe 3
      pts.head mustBe Point2D(2,1)
      pts.tail.head mustBe Point2D(3,2)
      pts.tail.tail.head mustBe Point2D(4,3)
    }

    "return point in negative xy" in {
      val pts = BattleShip(Point2D(4,3),Point2D(2,1)).allCells
      pts.length mustBe 3
      pts.head mustBe Point2D(4,3)
      pts.tail.head mustBe Point2D(3,2)
      pts.tail.tail.head mustBe Point2D(2,1)
    }
  }

}
