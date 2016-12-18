package module

import javax.inject.Inject

import model.{BattleField, Player, Point2D, ResultShooting}

/**
  * Created by anton on 12/16/2016.
  */
class PlayerManger @Inject() (battleFieldManger: BattleFieldManger){

  //return the new players with the updated boards,base on the shooting, and the result fo the shooing
  def sendShotToPlayers(shooter: Player, shot: Point2D, opponents: Seq[Player]): (Player,Seq[(Player,ResultShooting)]) = {

    //send shot to opponents, gather result
    val newOpponentsResult = opponents.map (p=>{
      val bfWithRes = battleFieldManger.receiveShot(p.ownBoard,shot)
      (Player(p.name,bfWithRes._1,p.opponentsBoards),bfWithRes._2)
      })

    //update shooter opponents' Board
    val mapOfOpponents = newOpponentsResult.map( pr=> (pr._1.name,pr._2)).toMap

    val newOpponentsBattleFields = shooter.opponentsBoards.map(kv => {
      mapOfOpponents.get(kv._1).map (r => (kv._1,battleFieldManger.newBattleFiledAfterShooting(kv._2,shot,r)))
    }).filter(_.isDefined).map(_.get).toList.toMap

    (Player(shooter.name,shooter.ownBoard,newOpponentsBattleFields),
      newOpponentsResult)
  }

  def  setupOpponentsBoards(players:Seq[Player],boardSize:Int):Seq[Player]={
    players.map{p=>
      val opponentBoards = players.filter(_.name!=p.name).map(pp=>(pp.name,BattleField(boardSize,List()))).toMap
      Player(p.name,p.ownBoard,opponentBoards)
    }
  }

}