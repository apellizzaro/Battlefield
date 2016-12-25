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

  "A ship" must {
    "occupy spaces" in {
      val results =List (BattleField.occupySpace(BattleShip(Point2D(0,0),Point2D(5,5)),Point2D (2,2)),
                BattleField.occupySpace(BattleShip(Point2D(2,2),Point2D(5,2)),Point2D (5,2)),
                BattleField.occupySpace(BattleShip(Point2D(2,2),Point2D(5,2)),Point2D (2,2)),
                BattleField.occupySpace(BattleShip(Point2D(2,2),Point2D(5,2)),Point2D (3,2)))
      results.forall(p=>p) mustBe true
    }

    "Not occupy  spaces" in {
      val results = List(BattleField.occupySpace(BattleShip(Point2D(2, 2), Point2D(5, 2)), Point2D(2, 3)),
        BattleField.occupySpace(BattleShip(Point2D(2, 2), Point2D(5, 2)), Point2D(5, 1)),
        BattleField.occupySpace(BattleShip(Point2D(0, 0), Point2D(5, 5)), Point2D(0, 1)),
        BattleField.occupySpace(BattleShip(Point2D(0, 0), Point2D(5, 5)), Point2D(4, 5)))

      results.forall(p => !p) mustBe true
    }

    "correct Length" in {
      val ship1Length=BattleShip (Point2D(1,1),Point2D(5,5)).length
      val ship2Length=BattleShip (Point2D(1,1),Point2D(6,1)).length
      val ship3Length=BattleShip (Point2D(1,1),Point2D(1,6)).length

      ship1Length mustBe 5
      ship2Length mustBe 6
      ship3Length mustBe 6

    }
  }
}
