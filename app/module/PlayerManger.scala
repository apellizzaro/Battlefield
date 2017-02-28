package module

import javax.inject.Inject

import model.{BattleField, Player, Point2D, ResultShooting}

/**
  * Created by anton on 12/16/2016.
  */
class PlayerManger @Inject() (battleFieldManger: BattleFieldManger){

  //return the new players with the updated boards,based on the shooting, and the result fo the shooting for each player
  //i.e. the new players (willhave na updatd board) amd the result(missed, hit, sunk..)
  def sendShotToPlayers(shooter: Player, shot: Point2D, opponents: Seq[Player]): (Player,Seq[(Player,ResultShooting)]) = {

    //send shot to opponents, gather result
    val newOpponentsResult = opponents.map (p => {
      val bfWithRes = battleFieldManger.receiveShot(p.ownBoard,shot)
      (Player(p.name,bfWithRes._1, p.opponentsBoards), bfWithRes._2,bfWithRes._3)
      })

    //update shooter opponents' Board
    val mapOfOpponents = newOpponentsResult.map( pr=> (pr._1.name,pr._2)).toMap

    val newOpponentsBattleFields = shooter.opponentsBoards.map(kv => {
      mapOfOpponents.get(kv._1). //find the board for that player (kv._1 is the player name
        map(r => (kv._1, //map it to a Tuple2 of playerName, Battlefield update with the result of shooting
        battleFieldManger.newBattleFiledAfterShooting(kv._2, //existing battlefield
          shot, r, //shot, Result shooting
          newOpponentsResult.find(_._1.name == kv._1).map(_._3).getOrElse(List()) //find if a ship got sunked, we need to update all the points
        )))
      }).filter(_.isDefined).map(_.get).toList.toMap

    (Player(shooter.name,shooter.ownBoard,newOpponentsBattleFields),
      newOpponentsResult.map( rs=>(rs._1,rs._2) ))
  }

  /*
  * Sets up the the player's opponents' boards when a gae is started.
  * the oppoents' boards are initialized all empty
  */
  def  setupOpponentsBoards(players:Seq[Player],boardSize:Int):Seq[Player]={
    players.map{p=>
      val opponentBoards = players.filter(_.name!=p.name).map(pp=>(pp.name,BattleField(boardSize,List()))).toMap
      Player(p.name,p.ownBoard,opponentBoards)
    }
  }

}