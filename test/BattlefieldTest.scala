import model.{BattleField, BattleFieldWithValidation, BattleShip, Point2D}
import org.scalatestplus.play.PlaySpec

/**
  * Created by apellizz on 12/21/16.
  */
class BattlefieldTest extends PlaySpec {


  "A ship" must {
    "occupy a space" in {
      val spaceOccupied = BattleField.occupySpace(BattleShip(Point2D(0, 0), Point2D(3, 3)), Point2D(2, 2))
      spaceOccupied mustBe true
    }


    "Not occupy a space" in {
      val spaceOccupied = BattleField.occupySpace(BattleShip(Point2D(0, 0), Point2D(3, 3)), Point2D(2, 3))
      spaceOccupied mustBe false
    }
  }

  "Battlefield creation" must {
    "not succeed with negative ships" in {
      val newBf =BattleFieldWithValidation(4,List(BattleShip(Point2D(0,-1),Point2D(2,2))))
      newBf mustBe Left("All ships must be inside the playing board")
    }

    "not succeed with ships out of grid" in {
      val newBf =BattleFieldWithValidation(4,List(BattleShip(Point2D(5,5),Point2D(2,2))))
      newBf mustBe Left("All ships must be inside the playing board")
    }

    "succeed with ships inside the grid" in {
      val newBf = BattleFieldWithValidation(5,List(BattleShip(Point2D(0,0),Point2D(4,4)))) match {
        case Right(_) => true
        case Left(_) => false

      }
      newBf mustBe true
    }

  }

}
