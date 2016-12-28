package controllers

import javax.inject._

import akka.actor.ActorSystem
import module.GameManager
import play.api.mvc._



/**
  * Created by apellizz on 12/26/16.
  */
@Singleton
class UIController @Inject()(actorSystem: ActorSystem, gameManager:GameManager) extends Controller {

  def mainPage = Action {
    Ok(views.html.index("Wecome to Battlefield"))

  }

  def newGamePage = Action {
    Ok("New Game)")//views.html.NewGame ("New Game"))
  }


}
