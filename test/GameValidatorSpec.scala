import model._
import org.scalatestplus.play.PlaySpec

/**
  * Created by apellizz on 12/23/16.
  */
class GameValidatorSpec extends PlaySpec {

  "A game validator" must {
    "not validate bc too many ships" in {
      val gm = GameValidator ("name",5,GameSetup(BattleshipConfiguration(List(BattleshipModel(4,2),BattleshipModel(3,2),BattleshipModel(5,5))),Map()))
      val res = gm match {
        case Left (_) => true
        case _ => false
      }
      res mustBe true
    }

    "not validate bc ships outside grid" in {
      val gm = GameValidator ("name",5,GameSetup(BattleshipConfiguration(List(BattleshipModel(4,2),BattleshipModel(3,2),BattleshipModel(5,5))),Map()))
      val res = gm match {
        case Left (_) => true
        case _ => false
      }
      res mustBe true
    }

    "Not validate for config not to spec" in {
      val gm = GameValidator ("name",20,GameSetup(
        BattleshipConfiguration(
          List(BattleshipModel(2,2),
            BattleshipModel(3,2),
            BattleshipModel(1,3))),
        Map("palyer1"->List(
          BattleShip(Point2D(0,4),Point2D(0,5)), //2
          BattleShip(Point2D(0,0),Point2D(2,2)),//3
          BattleShip(Point2D(0,0),Point2D(2,2)),//3
          BattleShip(Point2D(3,3),Point2D(3,3)),//1
          BattleShip(Point2D(3,3),Point2D(4,4)),//1
          BattleShip(Point2D(3,3),Point2D(5,5))//1
        ))))

      val res = gm match {
        case Left (m) => println(m)
          true
        case _ => false
      }
      res mustBe true
    }

    "validate" in {
      val gm = GameValidator ("name",20,GameSetup(
        BattleshipConfiguration(
          List(BattleshipModel(2,2),
            BattleshipModel(3,2),
            BattleshipModel(1,3))),
        Map("palyer1"->List(
          BattleShip(Point2D(2,2),Point2D(3,2)), //2
          BattleShip(Point2D(0,4),Point2D(0,5)), //2
          BattleShip(Point2D(0,0),Point2D(2,2)),//3
          BattleShip(Point2D(0,0),Point2D(2,2)),//3
          BattleShip(Point2D(3,3),Point2D(3,3)),//1
          BattleShip(Point2D(4,4),Point2D(4,4)),//1
          BattleShip(Point2D(5,5),Point2D(5,5))//1
        ))))

      val res = gm match {
        case Left (m) => println(m)
          false
        case _ => true
      }
      res mustBe true
    }

  }

}
