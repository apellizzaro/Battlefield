import model.{BattleField, BattleShip, Point2D}
import org.scalatestplus.play.PlaySpec

/**
  * Created by apellizz on 12/21/16.
  */
class BattlefieldTest extends PlaySpec {


  "A ship" must {
    "occupy a space" in {
      val spaceOccupied = BattleField.occupySpace(BattleShip( Point2D(0,0), Point2D(3,3)), Point2D(2,2))
      spaceOccupied mustBe true
    }
  }

  "Not occupy a space" in {
    val spaceOccupied = BattleField.occupySpace(BattleShip( Point2D(0,0), Point2D(3,3)), Point2D(2,3))
    spaceOccupied mustBe false
  }


}
